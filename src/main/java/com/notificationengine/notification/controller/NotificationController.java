package com.notificationengine.notification.controller;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.dto.NotificationRequest;
import com.notificationengine.notification.dto.response.DeliveryAttemptResponse;
import com.notificationengine.notification.dto.response.UserNotificationDetailResponse;
import com.notificationengine.notification.dto.response.UserNotificationPageResponse;
import com.notificationengine.notification.mapper.NotificationMapper;
import com.notificationengine.notification.service.DeliveryHistoryService;
import com.notificationengine.notification.service.NotificationService;
import com.notificationengine.notification.service.UserNotificationQueryService;
import com.notificationengine.security.AuthenticatedUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificationMapper notificationMapper;
    private final DeliveryHistoryService deliveryHistoryService;
    private final UserNotificationQueryService userNotificationQueryService;

    public NotificationController(
            NotificationService notificationService,
            AuthenticatedUserService authenticatedUserService,
            NotificationMapper notificationMapper,
            DeliveryHistoryService deliveryHistoryService,
            UserNotificationQueryService userNotificationQueryService
    ) {
        this.notificationService = notificationService;
        this.authenticatedUserService = authenticatedUserService;
        this.notificationMapper = notificationMapper;
        this.deliveryHistoryService = deliveryHistoryService;
        this.userNotificationQueryService = userNotificationQueryService;

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
    public ResponseEntity<List<DeliveryAttemptResponse>> getDeliveryAttempts(
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

    @GetMapping
    public ResponseEntity<UserNotificationPageResponse> getNotifications(
            @RequestParam(required = false)
            NotificationStatus status,

            @RequestParam(required = false)
            NotificationChannel channel,

            @PageableDefault(
                    page = 0,
                    size = 10
            )
            Pageable pageable
    ) {

        UUID userId =
                authenticatedUserService
                        .getCurrentUser()
                        .getId();

        return ResponseEntity.ok(
                userNotificationQueryService.getNotifications(
                        userId,
                        status,
                        channel,
                        pageable
                )
        );
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<UserNotificationDetailResponse> getNotification(
            @PathVariable UUID notificationId
    ) {

        UUID userId =
                authenticatedUserService
                        .getCurrentUser()
                        .getId();

        return ResponseEntity.ok(
                userNotificationQueryService.getNotification(
                        userId,
                        notificationId
                )
        );
    }

}
