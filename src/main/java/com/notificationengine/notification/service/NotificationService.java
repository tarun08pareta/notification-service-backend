package com.notificationengine.notification.service;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.dto.NotificationRequest;
import com.notificationengine.notification.mapper.NotificationMapper;
import com.notificationengine.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository
            , NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public Notification createNotification(
            NotificationRequest request,
            UUID userId
    ) {
        Notification notification = notificationMapper.toEntity(request, userId);
        notification.setStatus(NotificationStatus.QUEUED);
        return notificationRepository.save(notification);

    }
}
