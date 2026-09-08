package com.notificationengine.notification.controller;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.dto.NotificationRequest;
import com.notificationengine.notification.mapper.NotificationMapper;
import com.notificationengine.notification.service.DeliveryHistoryService;
import com.notificationengine.notification.service.NotificationService;
import com.notificationengine.security.AuthenticatedUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificationMapper notificationMapper;
    private final DeliveryHistoryService deliveryHistoryService;

    public NotificationController(
            NotificationService notificationService,
            AuthenticatedUserService authenticatedUserService,
            NotificationMapper notificationMapper,
            DeliveryHistoryService deliveryHistoryService
    ) {
        this.notificationService = notificationService;
        this.authenticatedUserService = authenticatedUserService;
        this.notificationMapper = notificationMapper;
        this.deliveryHistoryService = deliveryHistoryService;

    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody NotificationRequest request
    ) {
//        UUID userId = UUID.randomUUID();  // modify this
        UUID userId = authenticatedUserService.getCurrentUser().getId();
        Notification notification = notificationService.createNotification(request, userId,
                idempotencyKey);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(notification);
    }

    @GetMapping("/{notificationId}/attempts")
    public ResponseEntity<?> getDeliveryAttempts(
            @PathVariable UUID notificationId
    ) {
        UUID userId = authenticatedUserService.getCurrentUser().getId();
        return ResponseEntity.ok(
                deliveryHistoryService.getAttempts(
                        notificationId,
                        userId
                )
        );

    }
}
