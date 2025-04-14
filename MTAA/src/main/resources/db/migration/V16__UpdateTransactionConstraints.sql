alter table transaction
    alter column category_id drop not null,
    alter column budget_id drop not null,
    alter column location_id drop not null
