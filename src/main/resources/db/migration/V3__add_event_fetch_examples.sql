INSERT INTO historical_event (
    title,
    event_date,
    description,
    location_id
)
SELECT
    'Maximilian imprisoned in Bruges',
    DATE '1488-02-05',
    'Maximilian is detained by the citizens of Bruges.',
    id
FROM location
WHERE name = 'Bruges';


INSERT INTO historical_event (
    title,
    event_date,
    description,
    location_id
)
SELECT
    'Pieter Lanchals executed',
    DATE '1488-03-22',
    'Execution of Pieter Lanchals during the Bruges crisis.',
    id
FROM location
WHERE name = 'Bruges';


INSERT INTO historical_event (
    title,
    event_date,
    description,
    location_id
)
SELECT
    'Battle of Nancy',
    DATE '1477-01-05',
    'Charles the Bold is defeated and killed at Nancy.',
    id
FROM location
WHERE name = 'Nancy';


INSERT INTO historical_event (
    title,
    event_date,
    description,
    location_id
)
SELECT
    'Sluis research context',
    NULL,
    'Context event used to observe loading of a different location.',
    id
FROM location
WHERE name = 'Sluis';