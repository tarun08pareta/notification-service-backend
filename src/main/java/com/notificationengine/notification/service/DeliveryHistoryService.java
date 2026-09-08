package com.notificationengine.notification.service;

import com.notificationengine.notification.domain.DeliveryAttempt;
import com.notificationengine.notification.dto.response.DeliveryAttemptResponse;
import com.notificationengine.notification.repository.DeliveryAttemptRepository;
import com.notificationengine.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeliveryHistoryService {
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final NotificationRepository notificationRepository;

    public DeliveryHistoryService(
            DeliveryAttemptRepository deliveryAttemptRepository,
            NotificationRepository notificationRepository) {
        this.deliveryAttemptRepository = deliveryAttemptRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<DeliveryAttemptResponse> getAttempts(
            UUID notificationId,
            UUID userId
    ) {
        notificationRepository.findByIdAndUserId(
                notificationId, userId
        ).orElseThrow(() ->
                new IllegalStateException("Notification not found")
        );
        return deliveryAttemptRepository
                .findTopByNotificationIdOrderByAttemptNumberDesc(notificationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DeliveryAttemptResponse toResponse(
            DeliveryAttempt attempt
    ) {
        return new DeliveryAttemptResponse(
                attempt.getId(),
                attempt.getProvider(),
                attempt.getAttemptNumber(),
                attempt.getStatus(),
                attempt.getErrorCode(),
                attempt.getErrorMessage(),
                attempt.getProviderMessageId(),
                attempt.getStartedAt(),
                attempt.getCompletedAt()
        );
    }
}
