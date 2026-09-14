-- PostgreSQL is canonical. This layer preserves legacy identifiers and junctions.
create table entity (
    id bigserial primary key,
    public_id uuid not null unique default gen_random_uuid(),
    entity_type varchar(50) not null check (entity_type in ('PERSON', 'PLACE', 'INSTITUTION', 'OFFICE', 'FACTION', 'OBJECT', 'RELIC', 'EVENT', 'WORK', 'DOCUMENT', 'OTHER')),
    canonical_name varchar(255) not null,
    description text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index idx_entity_name on entity (canonical_name);

create table entity_alias (
    id bigserial primary key,
    entity_id bigint not null references entity(id) on delete cascade,
    alias varchar(255) not null,
    language varchar(20),
    alias_type varchar(50)
);
create unique index uq_entity_alias on entity_alias (entity_id, alias, coalesce(language, ''), coalesce(alias_type, ''));
create index idx_entity_alias_lookup on entity_alias (lower(alias));

-- One entity per typed row, including duplicate names. Do not infer identity.
alter table person add column entity_id bigint references entity(id);
alter table location add column entity_id bigint references entity(id);
alter table historical_event add column entity_id bigint references entity(id);

do $$
declare
    row_data record;
    new_entity_id bigint;
begin
    for row_data in select id, name as name, notes as description from person loop
        insert into entity (entity_type, canonical_name, description)
            values ('PERSON', row_data.name, row_data.description) returning id into new_entity_id;
        update person set entity_id = new_entity_id where id = row_data.id;
    end loop;
    for row_data in select id, name as name, notes as description from location loop
        insert into entity (entity_type, canonical_name, description)
            values ('PLACE', row_data.name, row_data.description) returning id into new_entity_id;
        update location set entity_id = new_entity_id where id = row_data.id;
    end loop;
    for row_data in select id, title as name, description as description from historical_event loop
        insert into entity (entity_type, canonical_name, description)
            values ('EVENT', row_data.name, row_data.description) returning id into new_entity_id;
        update historical_event set entity_id = new_entity_id where id = row_data.id;
    end loop;
end $$;

-- Keep legacy SQL and JPA inserts working without a new required input field.
-- Names/descriptions on typed rows remain authoritative for their generic mirror.
create function sync_typed_knowledge_entity() returns trigger language plpgsql as $$
declare
    entity_name text := to_jsonb(new) ->> TG_ARGV[1];
    entity_description text := to_jsonb(new) ->> TG_ARGV[2];
begin
    if TG_OP = 'UPDATE' and new.entity_id is distinct from old.entity_id then
        raise exception 'A typed row cannot change its knowledge entity';
    end if;
    if new.entity_id is null then
        insert into entity (entity_type, canonical_name, description)
            values (TG_ARGV[0], entity_name, entity_description) returning id into new.entity_id;
    else
        perform 1 from entity where id = new.entity_id and entity_type = TG_ARGV[0];
        if not found then
            raise exception 'Knowledge entity type must be %', TG_ARGV[0];
        end if;
        update entity set canonical_name = entity_name, description = entity_description, updated_at = now()
            where id = new.entity_id;
    end if;
    return new;
end $$;

alter table person alter column entity_id set not null;
alter table person add constraint uq_person_entity unique (entity_id);
create trigger trg_person_entity before insert or update on person
    for each row execute function sync_typed_knowledge_entity('PERSON', 'name', 'notes');

alter table location alter column entity_id set not null;
alter table location add constraint uq_location_entity unique (entity_id);
create trigger trg_location_entity before insert or update on location
    for each row execute function sync_typed_knowledge_entity('PLACE', 'name', 'notes');

alter table historical_event alter column entity_id set not null;
alter table historical_event add constraint uq_historical_event_entity unique (entity_id);
create trigger trg_historical_event_entity before insert or update on historical_event
    for each row execute function sync_typed_knowledge_entity('EVENT', 'title', 'description');

create table source (
    id bigserial primary key,
    public_id uuid not null unique default gen_random_uuid(),
    source_type varchar(50) not null,
    title varchar(500) not null,
    author varchar(500),
    publication_year integer,
    publisher varchar(500),
    url text,
    bibliographic_note text,
    notes text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create table citation (
    id bigserial primary key,
    public_id uuid not null unique default gen_random_uuid(),
    source_id bigint not null references source(id),
    locator varchar(255),
    page_start integer,
    page_end integer,
    quotation text,
    quotation_language varchar(20),
    notes text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_citation_pages check (page_start is null or page_end is null or page_start <= page_end)
);
create index idx_citation_source on citation(source_id);
create table claim (
    id bigserial primary key,
    public_id uuid not null unique default gen_random_uuid(),
    subject_entity_id bigint not null references entity(id),
    predicate varchar(100) not null,
    object_entity_id bigint references entity(id),
    literal_value text,
    claim_type varchar(50) not null check (claim_type in ('HISTORICAL_FACT', 'HISTORICAL_INFERENCE', 'GAME_INVENTION')),
    evidence_status varchar(50) not null check (evidence_status in ('DOCUMENTED', 'PROBABLE', 'PLAUSIBLE', 'SPECULATIVE', 'DISPUTED', 'FICTIONAL')),
    valid_from date,
    valid_to date,
    date_precision varchar(50),
    date_note text,
    notes text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_claim_object check ((object_entity_id is not null) <> (literal_value is not null)),
    constraint ck_claim_dates check (valid_from is null or valid_to is null or valid_from <= valid_to)
);
create index idx_claim_subject on claim(subject_entity_id);
create index idx_claim_object on claim(object_entity_id);
create table claim_evidence (
    claim_id bigint not null references claim(id) on delete cascade,
    citation_id bigint not null references citation(id) on delete cascade,
    evidence_type varchar(50) not null check (evidence_type in ('SUPPORTS', 'CONTRADICTS', 'CONTEXT')),
    strength varchar(50),
    notes text,
    primary key (claim_id, citation_id, evidence_type)
);
create index idx_claim_evidence_citation on claim_evidence(citation_id);
create table research_item_claim (
    research_item_id bigint not null references research_item(id) on delete cascade,
    claim_id bigint not null references claim(id) on delete cascade,
    primary key (research_item_id, claim_id)
);
create index idx_research_item_claim_claim on research_item_claim(claim_id);
