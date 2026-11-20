CREATE TABLE warranty_reminders (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  receipt_id UUID NOT NULL REFERENCES receipts(id) ON DELETE CASCADE,
  reminder_at TIMESTAMPTZ NOT NULL,
  interval_label VARCHAR(30) NOT NULL,
  delivered BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX warranty_reminders_due_idx ON warranty_reminders (reminder_at, delivered);
