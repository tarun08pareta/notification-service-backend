ALTER TABLE notification_provider_states
    ADD COLUMN priority INTEGER NOT NULL DEFAULT 0;

ALTER TABLE notification_provider_states
    ADD CONSTRAINT chk_notification_provider_states_priority
        CHECK (priority >= 0);