# Historical Figures API

Small Spring Boot research database for a historical-fantasy RPG set primarily in Bruges in 1488.

This project is intentionally simple. It uses conventional Spring layers and avoids extra infrastructure so the code is easy to read while learning Spring Boot, JPA/Hibernate, Maven, and PostgreSQL.

## Prerequisites

Install only these tools on the host machine:

- VS Code
- Docker or Docker Desktop
- Git

Java, Maven, and PostgreSQL run inside Docker.

## Development Environment

Open this folder in VS Code and choose **Reopen in Container** when prompted.

Your Git identity is restored on every container start, including after a rebuild.
Copy `.env.example` to `.env.local` in the project root and set `GIT_USER_NAME`
and `GIT_USER_EMAIL`. The local file is ignored by Git and stays in your host
workspace across rebuilds. It is sourced by Bash, so use quoted Bash assignments.
To apply changes in a running container, run `bash .devcontainer/post-start.sh`.

When opening this project directly from Windows, VS Code runs
`.devcontainer/update-wsl.ps1` on the host before starting the container. It runs
[`wsl --update`](https://learn.microsoft.com/en-us/windows/wsl/basic-commands#update-wsl)
to check for and install available WSL updates, then shows an eight-second Windows
popup. Progress and results also appear in **Dev Containers: Show Container Log**.
This checks WSL itself; Linux distribution packages have their own updates.

If the update fails or returns a reboot-required status, container startup
continues and the script shows a warning. Follow the popup instructions after
the container opens; Windows may require administrator rights for the update. The
script does not shut down WSL or restart Windows automatically. The hook can also
run when reopening an existing container. Starting services manually with Docker
Compose bypasses this VS Code hook.

To run the same check manually from Windows PowerShell in the project root:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .devcontainer/update-wsl.ps1
```

The startup hook requires Windows PowerShell on the host, matching this project's
Windows-based workflow.

The Dev Container starts two Docker Compose services:

- `app`: Java 21, Maven, Git, common command-line utilities, source code, and development tools
- `postgres`: PostgreSQL with persistent data in the `postgres-data` Docker volume

Inside the Dev Container, verify the Java and Maven tools:

```bash
java --version
mvn --version
```

Verify PostgreSQL is reachable from the Dev Container:

```bash
pg_isready -h postgres -p 5432 -U rpg -d historical_rpg
```

## Start PostgreSQL

VS Code Dev Containers starts the Compose stack automatically. From the host, you can also start it manually:

```bash
docker compose -f .devcontainer/docker-compose.yml up -d postgres
```

## Run the Application

Run this from inside the Dev Container:

```bash
mvn spring-boot:run
```

The API is available from the host browser at:

```text
http://localhost:8080
```

## Flyway Migrations

Flyway runs automatically when Spring Boot starts. It reads SQL files from:

```text
src/main/resources/db/migration
```

The first migration creates the schema:

```text
V1__create_initial_schema.sql
```

The second migration seeds small starter data:

```text
V2__seed_initial_data.sql
```

Hibernate is configured with `ddl-auto: validate`, so it checks that the Java entity mapping matches the database instead of creating or changing tables.

## Example Curl Commands

```bash
curl http://localhost:8080/api/research-items
curl http://localhost:8080/api/research-items/RES-095
curl http://localhost:8080/api/people
curl http://localhost:8080/api/locations
curl http://localhost:8080/api/events
```

Create a research item:

```bash
curl -i -X POST http://localhost:8080/api/research-items \
  -H "Content-Type: application/json" \
  -d '{"code":"RES-200","title":"Guild records","status":"OPEN","summary":"Check archive references."}'
```

Update a research item:

```bash
curl -i -X PUT http://localhost:8080/api/research-items/RES-200 \
  -H "Content-Type: application/json" \
  -d '{"code":"RES-200","title":"Guild records","status":"IN_PROGRESS","summary":"Archive review started."}'
```

Delete a research item:

```bash
curl -i -X DELETE http://localhost:8080/api/research-items/RES-200
```

## Project Architecture

The request flow is:

```text
REST Controller -> Service -> Spring Data JPA Repository -> Hibernate/JPA -> PostgreSQL
```

Package layout:

- `controller`: HTTP endpoints
- `service`: application logic and transaction boundaries
- `repository`: Spring Data database access interfaces
- `entity`: JPA classes mapped to database tables
- `dto`: request and response objects used by controllers
- `exception`: simple API error handling

## Spring Concepts for .NET Developers

`@Entity` marks a Java class as a database-mapped JPA entity. This is close to an Entity Framework entity class.

`JpaRepository` is a Spring Data interface that provides common CRUD operations and query-method generation. It is roughly comparable to a focused EF `DbSet<T>` plus repository-style helpers.

`@Service` marks a class as an application service managed by Spring dependency injection. This is similar to registering a service class in ASP.NET Core DI.

`@RestController` marks a class whose methods handle HTTP requests and return JSON responses. This is close to an ASP.NET Core API controller.

`@Transactional` defines a database transaction boundary. It is similar in purpose to wrapping EF Core changes in a transaction, but Spring applies it declaratively around service methods.

JPA is the Java persistence specification. Hibernate is the JPA implementation used here. Spring Data JPA builds repository abstractions on top of JPA/Hibernate. Spring Boot wires the application together and supplies sensible defaults.

## Tests

Run tests from inside the Dev Container while PostgreSQL is running:

```bash
mvn test
```

Build the application:

```bash
mvn package
```

The repository test uses the Compose PostgreSQL database because this project is meant to teach the real PostgreSQL-backed stack from the start.

## Historical Knowledge Base (KB-001)

PostgreSQL is the canonical database of record. A future Neo4j graph, vector
index, or CIDOC CRM/RDF export will be a derived projection, not a competing
source of truth. These integrations are outside this implementation.

`V4__add_knowledge_base_core.sql` adds the knowledge base without modifying
V1–V3, existing primary keys, or legacy junction tables:

- `entity` provides a stable UUID, type, name, and description for claim subjects
  and objects. `entity_alias` stores alternative names with optional language and
  alias type; lookup supports case-insensitive searches, and duplicate aliases
  for the same entity/language/type are rejected (including absent metadata).
- Existing people, locations, and events each receive a distinct entity, even
  when names match. Their `entity_id` links are unique and mandatory. Database
  triggers also create links for future legacy inserts and mirror name/description
  updates. Typed rows own these mirrored fields. JPA exposes a read-only
  `knowledgeEntity` association; flush and refresh a newly inserted typed row
  before accessing its database-generated link. Deleting a typed row retains
  its generic entity so that claims and their provenance remain available.
- `source` holds bibliographic metadata; `citation` holds a locator and optional
  extract within that source. `sourceType` is an extensible string, for example
  `BOOK`, `ARTICLE`, `PRIMARY_DOCUMENT`, `ARCHIVAL_ITEM`, `WEBSITE`, or `PODCAST`.
- `claim` stores a subject, predicate, and exactly one entity object or literal
  value. `claimType` (`HISTORICAL_FACT`, `HISTORICAL_INFERENCE`, `GAME_INVENTION`)
  is separate from `evidenceStatus` (`DOCUMENTED`, `PROBABLE`, `PLAUSIBLE`,
  `SPECULATIVE`, `DISPUTED`, `FICTIONAL`). Neither dimension is derived from the
  legacy `historical` flag or narrative notes.
- `validFrom`/`validTo` describe historical time, independently of record creation
  and update timestamps. Either bound may be absent. `datePrecision` is an
  extensible string such as `EXACT`, `CIRCA`, `BEFORE`, `AFTER`, or `RANGE`;
  `dateNote` preserves the researcher's uncertainty. Supplied ranges must be
  ordered. No precision is inferred from the date values.
- `claim_evidence` relates citations to claims as `SUPPORTS`, `CONTRADICTS`, or
  `CONTEXT`, with optional strength and notes. Opposing evidence may coexist;
  the API does not automatically resolve conflicts or change evidence status.
- `research_item_claim` lets multiple research items share a claim. Deleting a
  research item removes its links, not the underlying claims or sources.

Read an item's claims with:

```bash
curl http://localhost:8080/api/research-items/RES-095/claims
```

The response includes `code`, `title`, and a `claims` array. Each claim exposes
its `publicId`, `subject` and optional `object` (`publicId`, `name`, `type`),
`predicate`, optional `literalValue`, both classification dimensions, historical
date metadata, notes, and `evidence`. Each evidence entry includes its type,
strength, notes, and a `citation` containing the locator, extract, and nested
bibliographic `source`. Existing items without claims return an empty array;
unknown codes return 404. DTOs are mapped within a read-only service transaction
with Open Session in View disabled. There is no generic knowledge-base write API
yet; the JPA model and repositories support persistence.

V4 seeds no sources, citations, or claims. All new historical-shaped assertions
in tests are synthetic. RES-065 — Wolfgang von Polheim remains the first planned
real ingestion/validation case before bulk ingestion of the Bruges 1488 corpus.

The test suite includes a V3-to-V4 upgrade in a temporary PostgreSQL schema,
legacy insert/update compatibility, claim persistence and constraints, shared
claims, conflicting citations, and real HTTP-controller serialization after the
fixture transaction has closed. It requires the same PostgreSQL connection as
the existing repository tests. The migration test drops only its own temporary
schema; other test fixtures are rolled back or explicitly removed.
