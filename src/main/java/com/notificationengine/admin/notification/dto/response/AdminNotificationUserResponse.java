package com.notificationengine.admin.notification.dto.response;

import com.notificationengine.user.domain.AuthProvider;
import com.notificationengine.user.domain.UserStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class AdminNotificationUserResponse {
    private final UUID id;
    private final String name;
    private final String email;
    private final AuthProvider authProvider;
    private final UserStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public AdminNotificationUserResponse(
            UUID id,
            String name,
            String email,
            AuthProvider authProvider,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.authProvider = authProvider;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
