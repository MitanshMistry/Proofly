CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(320) NOT NULL UNIQUE,
  display_name VARCHAR(120) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE merchants (
  id UUID PRIMARY KEY,
  normalized_name VARCHAR(180) NOT NULL UNIQUE,
  display_name VARCHAR(180) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE projects (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name VARCHAR(120) NOT NULL,
  color VARCHAR(30) NOT NULL DEFAULT 'purple',
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (user_id, name)
);
CREATE TABLE receipts (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  merchant_id UUID REFERENCES merchants(id) ON DELETE SET NULL,
  project_id UUID REFERENCES projects(id) ON DELETE SET NULL,
  image_object_key VARCHAR(500),
  purchased_at DATE,
  currency CHAR(3) NOT NULL DEFAULT 'USD',
  subtotal NUMERIC(12, 2),
  tax NUMERIC(12, 2),
  total NUMERIC(12, 2),
  category VARCHAR(60),
  notes VARCHAR(1000),
  status VARCHAR(30) NOT NULL DEFAULT 'NEEDS_REVIEW',
  extraction_confidence NUMERIC(5, 4),
  source_text TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE receipt_line_items (
  id UUID PRIMARY KEY,
  receipt_id UUID NOT NULL REFERENCES receipts(id) ON DELETE CASCADE,
  description VARCHAR(240) NOT NULL,
  quantity NUMERIC(10, 3),
  unit_price NUMERIC(12, 2),
  line_total NUMERIC(12, 2)
);
CREATE TABLE merchant_aliases (
  id UUID PRIMARY KEY,
  merchant_id UUID NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
  alias VARCHAR(180) NOT NULL UNIQUE
);
CREATE TABLE history_events (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  receipt_id UUID REFERENCES receipts(id) ON DELETE SET NULL,
  event_type VARCHAR(40) NOT NULL,
  description VARCHAR(500) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX receipts_user_purchased_idx ON receipts (user_id, purchased_at DESC NULLS LAST);
CREATE INDEX receipts_user_status_idx ON receipts (user_id, status);
CREATE INDEX receipts_merchant_idx ON receipts (merchant_id);
CREATE INDEX receipts_project_idx ON receipts (project_id);
CREATE INDEX history_user_created_idx ON history_events (user_id, created_at DESC);
