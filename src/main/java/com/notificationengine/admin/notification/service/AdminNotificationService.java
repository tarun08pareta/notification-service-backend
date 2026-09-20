package com.notificationengine.admin.notification.service;

import com.notificationengine.admin.notification.dto.response.*;
import com.notificationengine.notification.domain.DeliveryAttempt;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.repository.DeliveryAttemptRepository;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.notification.repository.NotificationSpecification;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;

    public AdminNotificationService(NotificationRepository notificationRepository,
                                    UserRepository userRepository,
                                    DeliveryAttemptRepository deliveryAttemptRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
    }

    // Returns paginated notifications for admin monitoring.
    @Transactional(readOnly = true)
    public AdminNotificationPageResponse getNotifications(
            NotificationStatus status,
            NotificationChannel channel,
            Pageable pageable
    ) {
        Specification<Notification> specification =
                (root, query, criteriaBuilder) -> null;

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

        return new AdminNotificationPageResponse(
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

    private AdminNotificationResponse toResponse(
            Notification notification
    ) {

        return new AdminNotificationResponse(
                notification.getId(),
                notification.getUserId(),
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


    // notification details
    @Transactional(readOnly = true)
    public AdminNotificationDetailResponse getNotification(
            UUID notificationId
    ) {
        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found: " + notificationId
                                )
                        );

        User user =
                userRepository.findById(notification.getUserId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "User not found for notification: "
                                                + notificationId
                                )
                        );

        AdminNotificationUserResponse userResponse =
                new AdminNotificationUserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAuthProvider(),
                        user.getStatus(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                );

        return new AdminNotificationDetailResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getTemplate(),
                notification.getVariables(),
                notification.getStatus(),
                notification.getRetryCount(),
                notification.getNextRetryAt(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                userResponse
        );
    }


    // notification attempt
    // UPDATED: Returns all delivery attempts for an admin-visible notification.
    @Transactional(readOnly = true)
    public List<AdminNotificationAttemptResponse> getNotificationAttempts(
            UUID notificationId
    ) {

        notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notification not found: " + notificationId
                        )
                );

        return deliveryAttemptRepository
                .findByNotificationIdOrderByAttemptNumberAsc(notificationId)
                .stream()
                .map(this::toAttemptResponse)
                .toList();
    }

    private AdminNotificationAttemptResponse toAttemptResponse(
            DeliveryAttempt attempt
    ) {

        return new AdminNotificationAttemptResponse(
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
