ALTER TABLE receipts ADD COLUMN receipt_number VARCHAR(120);
ALTER TABLE receipts ADD COLUMN product_name VARCHAR(240);
ALTER TABLE receipts ADD COLUMN brand VARCHAR(120);
ALTER TABLE receipts ADD COLUMN model_number VARCHAR(180);
ALTER TABLE receipts ADD COLUMN serial_number VARCHAR(180);
ALTER TABLE receipts ADD COLUMN sku VARCHAR(120);
ALTER TABLE receipts ADD COLUMN quantity INTEGER;
ALTER TABLE receipts ADD COLUMN warranty_provider VARCHAR(40) NOT NULL DEFAULT 'UNKNOWN';
ALTER TABLE receipts ADD COLUMN warranty_duration_months INTEGER;
ALTER TABLE receipts ADD COLUMN warranty_start_at DATE;
ALTER TABLE receipts ADD COLUMN warranty_end_at DATE;
ALTER TABLE receipts ADD COLUMN return_window_end_at DATE;
ALTER TABLE receipts ADD COLUMN warranty_confirmed BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE purchase_documents (
  id UUID PRIMARY KEY,
  receipt_id UUID NOT NULL REFERENCES receipts(id) ON DELETE CASCADE,
  object_key VARCHAR(500) NOT NULL,
  mime_type VARCHAR(100) NOT NULL,
  document_type VARCHAR(40) NOT NULL,
  size_bytes BIGINT NOT NULL,
  sha256 VARCHAR(64) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX receipts_serial_idx ON receipts (serial_number);
CREATE INDEX receipts_model_idx ON receipts (model_number);
CREATE INDEX receipts_warranty_end_idx ON receipts (warranty_end_at);
CREATE INDEX purchase_documents_receipt_idx ON purchase_documents (receipt_id);
