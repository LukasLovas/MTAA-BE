alter table budget add column initial_amount bigint not null;
alter table budget add column lastResetDate timestamp without time zone not null;