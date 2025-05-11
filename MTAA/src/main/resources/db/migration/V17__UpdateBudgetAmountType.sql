alter table budget drop amount;
alter table budget add column amount numeric(12,2) not null default 0