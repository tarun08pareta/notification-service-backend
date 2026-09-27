package com.notificationengine.notification.repository;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class NotificationSpecification {

    private NotificationSpecification() {
    }

    // Builds optional status filter for admin notification monitoring.
    public static Specification<Notification> hasStatus(
            NotificationStatus status
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    // Builds optional channel filter for admin notification monitoring.
    public static Specification<Notification> hasChannel(
            NotificationChannel channel
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("channel"),
                        channel
                );
    }

    // Restricts notifications to one authenticated user.
    public static Specification<Notification> hasUserId(
            UUID userId
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("userId"),
                        userId
                );
    }
}