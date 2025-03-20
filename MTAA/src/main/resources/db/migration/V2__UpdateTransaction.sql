ALTER TABLE transaction
    ADD COLUMN date_created timestamp without time zone not null,
    ADD COLUMN transaction_type varchar(10) not null CHECK(transaction_type in ('EXPENSE', 'INCOME')),
    ADD COLUMN category_id varchar(50),
    ADD COLUMN budget_id bigserial, --TODO referencia na budgets
    ADD COLUMN frequency varchar(20) not null check(frequency in ('DEFAULT','UPCOMING','SUBSCRIPTION')),
    ADD COLUMN note varchar(150),
    ADD COLUMN attachment_id bigserial --TODO referencia na attachements