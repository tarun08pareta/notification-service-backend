CREATE TABLE api_tokens
(
    id           UUID PRIMARY KEY,
    user_id      UUID                     NOT NULL,
    name         VARCHAR(100)             NOT NULL,
    token_prefix VARCHAR(30)              NOT NULL,
    token_hash   VARCHAR(64)              NOT NULL UNIQUE,
    expires_at   TIMESTAMP WITH TIME ZONE,
    revoked_at   TIMESTAMP WITH TIME ZONE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_api_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

CREATE INDEX idx_api_tokens_user_id
    ON api_tokens (user_id);

CREATE INDEX idx_api_tokens_token_hash
    ON api_tokens (token_hash);