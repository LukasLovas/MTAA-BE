create table "category"(
    id bigserial primary key,
    label varchar(250) not null unique,
    user_id bigserial references "user"(id)
)