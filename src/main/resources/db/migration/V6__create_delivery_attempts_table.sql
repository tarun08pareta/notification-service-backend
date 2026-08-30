CREATE TABLE delivery_attempts
(
    id                  UUID PRIMARY KEY,

    notification_id     UUID         NOT NULL,

    provider            VARCHAR(100) NOT NULL,

    attempt_number      INTEGER      NOT NULL,

    status              VARCHAR(30)  NOT NULL,

    error_code          VARCHAR(100),

    error_message       TEXT,

    provider_message_id VARCHAR(255),

    started_at          TIMESTAMPTZ  NOT NULL,

    completed_at        TIMESTAMPTZ,

    CONSTRAINT fk_delivery_attempts_notification
        FOREIGN KEY (notification_id)
            REFERENCES notifications (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_delivery_attempts_status
        CHECK (
            status IN (
                       'PROCESSING',
                       'SUCCESS',
                       'FAILED'
                )
            ),

    CONSTRAINT chk_delivery_attempts_number
        CHECK (attempt_number > 0)
);

CREATE INDEX idx_delivery_attempts_notification_id
    ON delivery_attempts (notification_id);

CREATE INDEX idx_delivery_attempts_status
    ON delivery_attempts (status);

CREATE INDEX idx_delivery_attempts_provider
    ON delivery_attempts (provider);