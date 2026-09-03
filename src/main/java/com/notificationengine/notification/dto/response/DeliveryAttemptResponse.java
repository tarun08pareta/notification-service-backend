package com.notificationengine.notification.dto.response;

import com.notificationengine.notification.domain.DeliveryAttemptStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class DeliveryAttemptResponse {

    private UUID id;
    private String provider;
    private Integer attemptNumber;
    private DeliveryAttemptStatus status;
    private String errorCode;
    private String errorMessage;
    private String providerMessageId;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;

    public DeliveryAttemptResponse() {
    }

    public DeliveryAttemptResponse(
            UUID id,
            String provider,
            Integer attemptNumber,
            DeliveryAttemptStatus status,
            String errorCode,
            String errorMessage,
            String providerMessageId,
            OffsetDateTime startedAt,
            OffsetDateTime completedAt) {

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