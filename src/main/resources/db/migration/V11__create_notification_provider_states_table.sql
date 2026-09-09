CREATE TABLE notification_provider_states
(
    id         UUID PRIMARY KEY,

    provider   VARCHAR(100) NOT NULL,

    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_notification_provider_states_provider
        UNIQUE (provider)
);

CREATE INDEX idx_notification_provider_states_enabled
    ON notification_provider_states (enabled);