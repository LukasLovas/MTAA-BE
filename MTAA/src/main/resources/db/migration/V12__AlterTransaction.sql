alter table transaction add column location_id bigserial references location("id");