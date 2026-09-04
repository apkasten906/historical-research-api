insert into person (name, born_year, died_year, historical, notes) values
    ('Maximilian I', 1459, 1519, true, null),
    ('Olivier de la Marche', 1425, 1502, true, null),
    ('Thomas Everingham', null, null, true, null),
    ('Adolf of Cleves', 1425, 1492, true, null),
    ('Louis de Gruuthuse', 1427, 1492, true, null);

insert into location (name, location_type, modern_name, notes) values
    ('Bruges', 'city', 'Bruges', null),
    ('Craenenburg House', 'building', null, null),
    ('Church of St. Donatian', 'church', null, null),
    ('Nancy', 'city', 'Nancy', null),
    ('Sluis', 'town', 'Sluis', null);

insert into research_item (code, title, status, summary, created_at, updated_at) values
    ('RES-094', 'Croy Family Involvement', 'OPEN', null, now(), now()),
    ('RES-095', 'Thomas Everingham', 'IN_PROGRESS', null, now(), now()),
    ('RES-098', 'Sint-Basil', 'OPEN', null, now(), now()),
    ('RES-099', 'Sint-Donaas', 'PENDING_REVIEW', null, now(), now()),
    ('RES-108', 'Adolf of Cleves', 'OPEN', null, now(), now());

insert into historical_event (title, event_date, description, location_id)
select 'Bruges research context', null, null, id
from location
where name = 'Bruges';

insert into research_item_person (research_item_id, person_id)
select ri.id, p.id
from research_item ri
join person p on p.name = 'Thomas Everingham'
where ri.code = 'RES-095';

insert into research_item_person (research_item_id, person_id)
select ri.id, p.id
from research_item ri
join person p on p.name = 'Adolf of Cleves'
where ri.code = 'RES-108';

insert into research_item_location (research_item_id, location_id)
select ri.id, l.id
from research_item ri
join location l on l.name = 'Church of St. Donatian'
where ri.code = 'RES-099';
