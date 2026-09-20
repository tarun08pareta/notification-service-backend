package com.notificationengine.admin.notification.dto.response;

import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import lombok.Getter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
public class AdminNotificationDetailResponse {

    private final UUID id;
    private final NotificationChannel channel;
    private final String recipient;
    private final String template;
    private final Map<String, String> variables;
    private final NotificationStatus status;

    private final Integer retryCount;
    private final OffsetDateTime nextRetryAt;

    private final Instant createdAt;
    private final Instant updatedAt;

    private final AdminNotificationUserResponse user;

    public AdminNotificationDetailResponse(
            UUID id,
            NotificationChannel channel,
            String recipient,
            String template,
            Map<String, String> variables,
            NotificationStatus status,
            Integer retryCount,
            OffsetDateTime nextRetryAt,
            Instant createdAt,
            Instant updatedAt,
            AdminNotificationUserResponse user
    ) {
        this.id = id;
        this.channel = channel;
        this.recipient = recipient;
        this.template = template;
        this.variables = variables;
        this.status = status;
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }
}