alter table transaction
    drop column amount,
    add column amount numeric(10,2) not null;
alter table budget
    drop column amount,
    drop column initial_amount,
    add column amount numeric(10,2) not null,
    add column initial_amount numeric(10,2) not null;
alter table "user"
    add column currency varchar(3) default 'EUR';