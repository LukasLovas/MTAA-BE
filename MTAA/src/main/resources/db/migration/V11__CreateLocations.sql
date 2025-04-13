create table location(
    id bigserial not null primary key,
    name varchar(255),
    latitude decimal(8,6) not null,
    longitude decimal(9,6) not null
)