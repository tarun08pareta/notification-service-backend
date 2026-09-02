ALTER TABLE notifications
    ADD CONSTRAINT uk_notifications_user_idempotency
        UNIQUE (user_id, idempotency_key);