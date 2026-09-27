package com.notificationengine.notification.dto.response;

import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {

    private UUID id;
    private NotificationChannel channel;
    private String recipient;
    private String template;
    private NotificationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer retryCount;
    private OffsetDateTime nextRetryAt;
}