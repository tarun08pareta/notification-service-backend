package com.notificationengine.notification.repository;

import com.notificationengine.notification.domain.DeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, UUID> {

    List<DeliveryAttempt> findByNotificationIdOrderByAttemptNumberAsc(
            UUID notificationId
    );
}
