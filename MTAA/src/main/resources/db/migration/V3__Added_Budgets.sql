ALTER TABLE transaction
    DROP CONSTRAINT transaction_frequency_check,
    DROP CONSTRAINT transaction_transaction_type_check;

CREATE TABLE budget(
    id BIGSERIAL PRIMARY KEY,
    label varchar(150),
    amount bigint,
    start_date timestamp without time zone not null,
    interval_value INTEGER NOT NULL,
    interval_enum INTEGER NOT NULL
)