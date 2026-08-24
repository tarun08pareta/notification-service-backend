CREATE TABLE users
(
    id               UUID PRIMARY KEY,
    name             VARCHAR(150)             NOT NULL,
    email            VARCHAR(255)             NOT NULL UNIQUE,
    password         VARCHAR(255),
    auth_provider    VARCHAR(20)              NOT NULL,
    provider_user_id VARCHAR(255),
    status     VARCHAR(30)              NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);