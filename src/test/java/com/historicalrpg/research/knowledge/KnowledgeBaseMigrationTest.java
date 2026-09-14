package com.historicalrpg.research.knowledge;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

class KnowledgeBaseMigrationTest {
    @Test
    void upgradesPopulatedV3AndPreservesLegacyRowsAndFutureWrites() throws Exception {
        String url = System.getProperty("spring.datasource.url", "jdbc:postgresql://"
                + env("DB_HOST", "postgres") + ":" + env("DB_PORT", "5432") + "/"
                + env("DB_NAME", "historical_rpg"));
        String username = System.getProperty("spring.datasource.username", env("DB_USERNAME", "rpg"));
        String password = System.getProperty("spring.datasource.password", env("DB_PASSWORD", "rpg"));
        String schema = "kb_migration_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = DriverManager.getConnection(url, username, password);
                Statement sql = connection.createStatement()) {
            try {
                Flyway.configure().dataSource(url, username, password).schemas(schema).defaultSchema(schema)
                        .target("3").load().migrate();
                sql.execute("set search_path to " + schema);
                // Duplicate names must remain separate identities; these are only test records.
                sql.execute("insert into person (name, historical) values ('Synthetic duplicate', true), ('Synthetic duplicate', false)");
                long people = count(sql, "select count(*) from person");
                long locations = count(sql, "select count(*) from location");
                long events = count(sql, "select count(*) from historical_event");
                long personLinks = count(sql, "select count(*) from research_item_person");
                long locationLinks = count(sql, "select count(*) from research_item_location");
                long eventLinks = count(sql, "select count(*) from event_person");

                var result = Flyway.configure().dataSource(url, username, password)
                        .schemas(schema).defaultSchema(schema).load().migrate();
                assertThat(result.migrationsExecuted).isEqualTo(1);
                assertThat(count(sql, "select count(*) from person")).isEqualTo(people);
                assertThat(count(sql, "select count(*) from location")).isEqualTo(locations);
                assertThat(count(sql, "select count(*) from historical_event")).isEqualTo(events);
                assertThat(count(sql, "select count(*) from entity")).isEqualTo(people + locations + events);
                assertThat(count(sql, "select count(*) from research_item_person")).isEqualTo(personLinks);
                assertThat(count(sql, "select count(*) from research_item_location")).isEqualTo(locationLinks);
                assertThat(count(sql, "select count(*) from event_person")).isEqualTo(eventLinks);
                assertThat(count(sql, "select count(*) from claim")).isZero();
                assertThat(count(sql, "select count(*) from source")).isZero();
                assertMirror(sql, "person", "name", "PERSON", people);
                assertMirror(sql, "location", "name", "PLACE", locations);
                assertMirror(sql, "historical_event", "title", "EVENT", events);
                assertThat(count(sql, "select count(distinct entity_id) from person where name = 'Synthetic duplicate'"))
                        .isEqualTo(2);

                sql.execute("insert into person (name, historical) values ('Synthetic future person', true)");
                sql.execute("insert into location (name) values ('Synthetic future place')");
                sql.execute("insert into historical_event (title) values ('Synthetic future event')");
                sql.execute("update person set name = 'Synthetic renamed person' where name = 'Synthetic future person'");
                sql.execute("update location set name = 'Synthetic renamed place' where name = 'Synthetic future place'");
                sql.execute("update historical_event set title = 'Synthetic renamed event' where title = 'Synthetic future event'");
                assertMirror(sql, "person", "name", "PERSON", people + 1);
                assertMirror(sql, "location", "name", "PLACE", locations + 1);
                assertMirror(sql, "historical_event", "title", "EVENT", events + 1);
                assertThat(Flyway.configure().dataSource(url, username, password).schemas(schema)
                        .defaultSchema(schema).load().migrate().migrationsExecuted).isZero();
            } finally {
                sql.execute("drop schema if exists " + schema + " cascade");
            }
        }
    }

    private static void assertMirror(Statement sql, String table, String name, String type, long expected)
            throws Exception {
        assertThat(count(sql, "select count(*) from " + table + " t join entity e on e.id = t.entity_id "
                + "where e.entity_type = '" + type + "' and e.canonical_name = t." + name)).isEqualTo(expected);
    }

    private static long count(Statement sql, String query) throws Exception {
        try (ResultSet result = sql.executeQuery(query)) {
            result.next();
            return result.getLong(1);
        }
    }

    private static String env(String name, String fallback) {
        return System.getenv().getOrDefault(name, fallback);
    }
}
