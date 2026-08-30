package com.notificationengine.notification.controller;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.dto.NotificationRequest;
import com.notificationengine.notification.mapper.NotificationMapper;
import com.notificationengine.notification.service.NotificationService;
import com.notificationengine.security.AuthenticatedUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificationMapper notificationMapper;

    public NotificationController(
            NotificationService notificationService,
            AuthenticatedUserService authenticatedUserService,
            NotificationMapper notificationMapper
    ) {
        this.notificationService = notificationService;
        this.authenticatedUserService = authenticatedUserService;
        this.notificationMapper = notificationMapper;

    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @Valid @RequestBody NotificationRequest request
    ) {
//        UUID userId = UUID.randomUUID();  // modify this
        UUID userId = authenticatedUserService.getCurrentUser().getId();
        Notification notification = notificationService.createNotification(request, userId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(notification);
    }
}
