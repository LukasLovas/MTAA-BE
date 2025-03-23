alter table transaction
add column user_id bigserial references "user"(id);

alter table budget
add column user_id bigserial references "user"(id);