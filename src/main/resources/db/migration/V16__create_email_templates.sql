CREATE TABLE email_templates
(
    id         UUID PRIMARY KEY,

    code       VARCHAR(100) NOT NULL,
    name       VARCHAR(150) NOT NULL,

    subject    VARCHAR(500) NOT NULL,

    html_body  TEXT         NOT NULL,
    text_body  TEXT,

    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',

    version    INTEGER      NOT NULL DEFAULT 1,

    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_email_templates_code
        UNIQUE (code),

    CONSTRAINT chk_email_templates_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),

    CONSTRAINT chk_email_templates_version
        CHECK (version > 0)
);

CREATE INDEX idx_email_templates_status
    ON email_templates (status);

CREATE INDEX idx_email_templates_created_at
    ON email_templates (created_at);