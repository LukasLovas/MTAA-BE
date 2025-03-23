create table "user"(
    id bigserial primary key,
    username varchar(250) not null unique,
    password varchar(50) not null,
    enabled boolean not null default true
)