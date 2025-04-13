alter table budget add column initial_amount bigint not null;
alter table budget add column last_reset_date timestamp without time zone not null;