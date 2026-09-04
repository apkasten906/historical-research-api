create table research_item (
    id bigserial primary key,
    code varchar(50) not null unique,
    title varchar(255) not null,
    status varchar(50) not null,
    summary text,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table person (
    id bigserial primary key,
    name varchar(255) not null,
    born_year integer,
    died_year integer,
    historical boolean not null,
    notes text
);

create table location (
    id bigserial primary key,
    name varchar(255) not null,
    location_type varchar(255),
    modern_name varchar(255),
    notes text
);

create table historical_event (
    id bigserial primary key,
    title varchar(255) not null,
    event_date date,
    description text,
    location_id bigint references location(id)
);

create table research_item_person (
    research_item_id bigint not null references research_item(id) on delete cascade,
    person_id bigint not null references person(id) on delete cascade,
    primary key (research_item_id, person_id)
);

create table research_item_location (
    research_item_id bigint not null references research_item(id) on delete cascade,
    location_id bigint not null references location(id) on delete cascade,
    primary key (research_item_id, location_id)
);

create table event_person (
    historical_event_id bigint not null references historical_event(id) on delete cascade,
    person_id bigint not null references person(id) on delete cascade,
    primary key (historical_event_id, person_id)
);
