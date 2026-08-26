CREATE TABLE notifications
(
    id         UUID PRIMARY KEY,

    user_id    UUID         NOT NULL,

    channel    VARCHAR(20)  NOT NULL,

    recipient  VARCHAR(320) NOT NULL,

    template   VARCHAR(100) NOT NULL,

    variables  JSONB,

    status     VARCHAR(30)  NOT NULL,

    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT chk_notifications_channel
        CHECK (channel IN ('EMAIL', 'SMS')),

    CONSTRAINT chk_notifications_status
        CHECK (status IN ('QUEUED', 'PROCESSING', 'SENT', 'FAILED'))
);

CREATE INDEX idx_notifications_user_id
    ON notifications (user_id);

CREATE INDEX idx_notifications_status
    ON notifications (status);

CREATE INDEX idx_notifications_created_at
    ON notifications (created_at);