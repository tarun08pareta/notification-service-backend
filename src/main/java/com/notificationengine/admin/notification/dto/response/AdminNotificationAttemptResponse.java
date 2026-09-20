package com.notificationengine.admin.notification.dto.response;

import com.notificationengine.notification.domain.DeliveryAttemptStatus;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class AdminNotificationAttemptResponse {

    private final UUID id;
    private final String provider;
    private final Integer attemptNumber;
    private final DeliveryAttemptStatus status;
    private final String errorCode;
    private final String errorMessage;
    private final String providerMessageId;
    private final OffsetDateTime startedAt;
    private final OffsetDateTime completedAt;

    public AdminNotificationAttemptResponse(
            UUID id,
            String provider,
            Integer attemptNumber,
            DeliveryAttemptStatus status,
            String errorCode,
            String errorMessage,
            String providerMessageId,
            OffsetDateTime startedAt,
            OffsetDateTime completedAt
    ) {
        this.id = id;
        this.provider = provider;
        this.attemptNumber = attemptNumber;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.providerMessageId = providerMessageId;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }
}