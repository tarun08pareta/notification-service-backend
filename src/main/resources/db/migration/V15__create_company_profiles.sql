CREATE TABLE company_profiles
(
    id                UUID PRIMARY KEY,

    user_id           UUID         NOT NULL UNIQUE,

    company_name      VARCHAR(150) NOT NULL,

    logo_url          VARCHAR(2048),
    logo_public_id    VARCHAR(255),

    sender_name       VARCHAR(150),
    sender_email      VARCHAR(255),
    reply_to_email    VARCHAR(255),

    sms_sender_id     VARCHAR(50),
    sms_sender_number VARCHAR(50),

    created_at        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_company_profiles_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_company_profiles_user_id
    ON company_profiles (user_id);