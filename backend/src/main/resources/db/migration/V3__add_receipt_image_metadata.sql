ALTER TABLE receipts ADD COLUMN image_mime_type VARCHAR(100);
ALTER TABLE receipts ADD COLUMN image_size_bytes BIGINT;
ALTER TABLE receipts ADD COLUMN image_sha256 VARCHAR(64);
