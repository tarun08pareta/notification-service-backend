package com.notificationengine.apiToken.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
public class ApiTokenCreateResponse {
    private UUID id;
    private String name;
    private String token;

    private Instant createdAt;
    private Instant expiresAt;

    public ApiTokenCreateResponse(
            UUID id,
            String name,
            String token,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
