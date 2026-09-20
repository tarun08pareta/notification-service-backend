package com.notificationengine.apiToken.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
public class ApiTokenListResponse {
    private UUID id;
    private String name;
    private String tokenPrefix;
    private String token;
    private Instant createdAt;
    private Instant lastUsedAt;
    private Instant expiresAt;
    private Instant revokedAt;

    public ApiTokenListResponse(
            UUID id,
            String name,
            String token,
            String tokenPrefix,
            Instant createdAt,
            Instant lastUsedAt,
            Instant expiresAt,
            Instant revokedAt
    ) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.tokenPrefix = tokenPrefix;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
    }
}
