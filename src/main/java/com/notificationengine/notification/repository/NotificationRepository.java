package com.notificationengine.notification.repository;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByStatusAndNextRetryAtLessThanEqual(
            NotificationStatus status,
            OffsetDateTime now
    );

    @Modifying
    @Query("""
                UPDATE Notification n
                SET n.status = :processingStatus
                WHERE n.id = :notificationId
                  AND n.status = :retryScheduledStatus
            """)
    int claimForProcessing(
            @Param("notificationId") UUID notificationId,
            @Param("processingStatus") NotificationStatus processingStatus,
            @Param("retryScheduledStatus") NotificationStatus retryScheduledStatus
    );
    Optional<Notification> findByUserIdAndIdempotencyKey(
            UUID userId,
            String idempotencyKey
    );

    Optional<Notification> findByIdAndUserId(
            UUID id,
            UUID userId
    );


}
