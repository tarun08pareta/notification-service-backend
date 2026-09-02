ALTER TABLE notifications
    ADD COLUMN retry_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE notifications
    ADD COLUMN next_retry_at TIMESTAMP WITH TIME ZONE;