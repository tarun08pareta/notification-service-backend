ALTER TABLE notifications
DROP
CONSTRAINT chk_notifications_status;

ALTER TABLE notifications
    ADD CONSTRAINT chk_notifications_status
        CHECK (
            status IN (
                       'QUEUED',
                       'PROCESSING',
                       'RETRY_SCHEDULED',
                       'SENT',
                       'FAILED'
                )
            );