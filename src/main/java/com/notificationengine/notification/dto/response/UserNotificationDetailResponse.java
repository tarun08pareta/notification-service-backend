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
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationDetailResponse {

    private UUID id;
    private NotificationChannel channel;
    private String recipient;
    private String template;
    private Map<String, String> variables;
    private Map<String, String> advancedVariables;
    private NotificationStatus status;
    private Integer retryCount;
    private OffsetDateTime nextRetryAt;
    private Instant createdAt;
    private Instant updatedAt;
}