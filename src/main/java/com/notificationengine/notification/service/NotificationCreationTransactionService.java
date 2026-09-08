package com.notificationengine.notification.service;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.dto.NotificationRequest;
import com.notificationengine.notification.mapper.NotificationMapper;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.notification.event.NotificationCreatedEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationCreationTransactionService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MeterRegistry meterRegistry;

    public NotificationCreationTransactionService(
            NotificationMapper notificationMapper,
            NotificationRepository notificationRepository,
            ApplicationEventPublisher eventPublisher,
            MeterRegistry meterRegistry
    ) {
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public Notification create(
            NotificationRequest request,
            java.util.UUID userId,
            String idempotencyKey
    ) {

        Notification notification =
                notificationMapper.toEntity(request, userId);

        notification.setStatus(NotificationStatus.QUEUED);
        notification.setIdempotencyKey(idempotencyKey);

        Notification savedNotification =
                notificationRepository.saveAndFlush(notification);
        meterRegistry.counter("notification.created").increment();

        eventPublisher.publishEvent(
                new NotificationCreatedEvent(savedNotification.getId())
        );

        return savedNotification;
    }
}