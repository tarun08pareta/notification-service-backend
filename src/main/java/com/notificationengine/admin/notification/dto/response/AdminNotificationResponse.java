package com.notificationengine.admin.notification.dto.response;

import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class AdminNotificationResponse {
    private UUID id;

    private UUID userId;

    private NotificationChannel channel;

    private String recipient;

    private String template;

    private NotificationStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer retryCount;

    private OffsetDateTime nextRetryAt;


    public AdminNotificationResponse(
            UUID id,
            UUID userId,
            NotificationChannel channel,
            String recipient,
            String template,
            NotificationStatus status,
            Instant createdAt,
            Instant updatedAt,
            Integer retryCount,
            OffsetDateTime nextRetryAt
    ) {
        this.id = id;
        this.userId = userId;
        this.channel = channel;
        this.recipient = recipient;
        this.template = template;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
    }
}
