ALTER TABLE transaction DROP COLUMN attachment_id;
ALTER TABLE transaction ADD COLUMN filename VARCHAR(255);