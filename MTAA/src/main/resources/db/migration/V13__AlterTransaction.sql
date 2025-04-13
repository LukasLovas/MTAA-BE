alter table transaction
drop category_id,
add column category_id bigserial references category("id"),
drop budget_id,
add column budget_id bigserial references budget("id")

