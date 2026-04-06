ALTER TABLE checklist_items
    ADD COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN completed_at TIMESTAMP NULL;

CREATE INDEX idx_checklist_items_completed ON checklist_items(completed);
