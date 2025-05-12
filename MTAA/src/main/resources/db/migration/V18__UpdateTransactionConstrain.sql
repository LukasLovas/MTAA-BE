alter table transaction
    drop constraint if exists transaction_budget_id_fkey;

alter table transaction
    add constraint transaction_budget_id_fkey
        foreign key (budget_id)
            references budget(id)
            on delete set null