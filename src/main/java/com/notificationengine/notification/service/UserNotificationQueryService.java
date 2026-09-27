package com.notificationengine.notification.service;


import com.notificationengine.notification.domain.DeliveryAttempt;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.dto.response.DeliveryAttemptResponse;
import com.notificationengine.notification.dto.response.UserNotificationDetailResponse;
import com.notificationengine.notification.dto.response.UserNotificationPageResponse;
import com.notificationengine.notification.dto.response.UserNotificationResponse;
import com.notificationengine.notification.repository.DeliveryAttemptRepository;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.notification.repository.NotificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserNotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;

    @Transactional(readOnly = true)
    public UserNotificationPageResponse getNotifications(
            UUID userId,
            NotificationStatus status,
            NotificationChannel channel,
            Pageable pageable
    ) {

        Specification<Notification> specification =
                NotificationSpecification.hasUserId(userId);

        if (status != null) {
            specification = specification.and(
                    NotificationSpecification.hasStatus(status)
            );
        }

        if (channel != null) {
            specification = specification.and(
                    NotificationSpecification.hasChannel(channel)
            );
        }

        Page<Notification> page =
                notificationRepository.findAll(
                        specification,
                        pageable
                );

        return new UserNotificationPageResponse(
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public UserNotificationDetailResponse getNotification(
            UUID userId,
            UUID notificationId
    ) {

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(
                                notificationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found: "
                                                + notificationId
                                )
                        );

        return new UserNotificationDetailResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getTemplate(),
                notification.getVariables(),
                notification.getAdvancedVariables(),
                notification.getStatus(),
                notification.getRetryCount(),
                notification.getNextRetryAt(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<DeliveryAttemptResponse> getNotificationAttempts(
            UUID userId,
            UUID notificationId
    ) {

        // IMPORTANT:
        // First verify that this notification belongs to the
        // authenticated user.
        notificationRepository
                .findByIdAndUserId(
                        notificationId,
                        userId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notification not found: "
                                        + notificationId
                        )
                );

        return deliveryAttemptRepository
                .findByNotificationIdOrderByAttemptNumberAsc(
                        notificationId
                )
                .stream()
                .map(this::toAttemptResponse)
                .toList();
    }

    private UserNotificationResponse toResponse(
            Notification notification
    ) {

        return new UserNotificationResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getTemplate(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.getRetryCount(),
                notification.getNextRetryAt()
        );
    }

    private DeliveryAttemptResponse toAttemptResponse(
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