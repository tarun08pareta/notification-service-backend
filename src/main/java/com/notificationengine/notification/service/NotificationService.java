package com.notificationengine.notification.service;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.dto.NotificationRequest;
import com.notificationengine.notification.event.NotificationCreatedEvent;
import com.notificationengine.notification.mapper.NotificationMapper;
import com.notificationengine.notification.repository.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationService(NotificationRepository notificationRepository
            , NotificationMapper notificationMapper,
                               ApplicationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Notification createNotification(
            NotificationRequest request,
            UUID userId
    ) {
        Notification notification = notificationMapper.toEntity(request, userId);
        notification.setStatus(NotificationStatus.QUEUED);
        Notification saveNotification = notificationRepository.save(notification);
        eventPublisher.publishEvent(
                new NotificationCreatedEvent(
                        saveNotification.getId()
                )
        );

        return saveNotification;

    }
}
