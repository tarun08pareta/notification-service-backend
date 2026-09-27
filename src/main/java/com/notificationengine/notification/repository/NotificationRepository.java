package com.notificationengine.notification.repository;

import com.notificationengine.admin.dashboard.dto.AdminDashboardAggregate;
import com.notificationengine.dashboard.dto.UserDashboardAggregate;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {
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

    //  Provides paginated notification listing for admin monitoring.
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // UPDATED: Returns notifications belonging only to the authenticated user.
    Page<Notification> findByUserIdOrderByCreatedAtDesc(
            UUID userId,
            Pageable pageable
    );

// for user dashboard
    @Query("""
        SELECT new com.notificationengine.dashboard.dto.UserDashboardAggregate(
            COUNT(n),
            SUM(CASE WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.SENT THEN 1 ELSE 0 END),
            SUM(CASE WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.FAILED THEN 1 ELSE 0 END),
            SUM(CASE WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.QUEUED THEN 1 ELSE 0 END),
            SUM(CASE WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.RETRY_SCHEDULED THEN 1 ELSE 0 END),
            SUM(CASE WHEN n.channel = com.notificationengine.notification.domain.NotificationChannel.EMAIL THEN 1 ELSE 0 END),
            SUM(CASE WHEN n.channel = com.notificationengine.notification.domain.NotificationChannel.SMS THEN 1 ELSE 0 END)
        )
        FROM Notification n
        WHERE n.userId = :userId
        """)
    UserDashboardAggregate getUserDashboardAggregate(
            @Param("userId") UUID userId
    );


    // for admin dashboard
    @Query("""
        SELECT new com.notificationengine.admin.dashboard.dto.AdminDashboardAggregate(
            COUNT(n),
            COALESCE(SUM(
                CASE
                    WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.SENT
                    THEN 1
                    ELSE 0
                END
            ), 0),
            COALESCE(SUM(
                CASE
                    WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.FAILED
                    THEN 1
                    ELSE 0
                END
            ), 0),
            COALESCE(SUM(
                CASE
                    WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.QUEUED
                    THEN 1
                    ELSE 0
                END
            ), 0),
            COALESCE(SUM(
                CASE
                    WHEN n.status = com.notificationengine.notification.domain.NotificationStatus.RETRY_SCHEDULED
                    THEN 1
                    ELSE 0
                END
            ), 0),
            COALESCE(SUM(
                CASE
                    WHEN n.channel = com.notificationengine.notification.domain.NotificationChannel.EMAIL
                    THEN 1
                    ELSE 0
                END
            ), 0),
            COALESCE(SUM(
                CASE
                    WHEN n.channel = com.notificationengine.notification.domain.NotificationChannel.SMS
                    THEN 1
                    ELSE 0
                END
            ), 0)
        )
        FROM Notification n
        """)
    AdminDashboardAggregate getAdminDashboardAggregate();

    // NEW: Returns recent notifications filtered by status.
    Page<Notification> findByStatusOrderByCreatedAtDesc(
            NotificationStatus status,
            Pageable pageable
    );
}
