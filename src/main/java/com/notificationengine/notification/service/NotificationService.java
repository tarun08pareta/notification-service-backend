package com.notificationengine.notification.service;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.dto.NotificationRequest;
import com.notificationengine.notification.event.NotificationCreatedEvent;
import com.notificationengine.notification.exception.IdempotencyConflictException;
import com.notificationengine.notification.mapper.NotificationMapper;
import com.notificationengine.notification.repository.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationCreationTransactionService notificationCreationTransactionService;

    public NotificationService(NotificationRepository notificationRepository
            , NotificationMapper notificationMapper,
                               ApplicationEventPublisher eventPublisher,
                               NotificationCreationTransactionService notificationCreationTransactionService) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.eventPublisher = eventPublisher;
        this.notificationCreationTransactionService =
                notificationCreationTransactionService;
    }


    public Notification createNotification(
            NotificationRequest request,
            UUID userId,
            String idempotencyKey
    ) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Idempotency-Key must not be blank"
            );
        }

        // maan lo unique id lekr chal rhe h
        Optional<Notification> existingNotification = notificationRepository.findByUserIdAndIdempotencyKey(
                userId,
                idempotencyKey
        );
        if (existingNotification.isPresent()) {

            Notification existing = existingNotification.get();

            if (!isSameRequest(existing, request)) {
                throw new IdempotencyConflictException(
                        "Idempotency-Key has already been used with different request data"
                );
            }

            return existing;
        }

//        Notification notification = notificationMapper.toEntity(request, userId);
//        notification.setStatus(NotificationStatus.QUEUED);
//        notification.setIdempotencyKey(idempotencyKey);
//        Notification saveNotification = notificationRepository.save(notification);
//        eventPublisher.publishEvent(
//                new NotificationCreatedEvent(
//                        saveNotification.getId()
//                )
//        );
//
//        return saveNotification;

        try {
            return notificationCreationTransactionService.create(
                    request,
                    userId,
                    idempotencyKey
            );
        } catch (DataIntegrityViolationException ex) {

            return notificationRepository
                    .findByUserIdAndIdempotencyKey(
                            userId,
                            idempotencyKey
                    )
                    .orElseThrow(() -> ex);
        }

    }

    private boolean isSameRequest(
            Notification existing,
            NotificationRequest request
    ) {
        return existing.getChannel() == request.getChannel()
                && existing.getRecipient().equals(request.getRecipient())
                && existing.getTemplate().equals(request.getTemplate())
                && java.util.Objects.equals(
                existing.getVariables(),
                request.getVariables()
        );
    }
}
