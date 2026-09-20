package com.notificationengine.admin.notification.controller;

import com.notificationengine.admin.notification.dto.response.AdminNotificationAttemptResponse;
import com.notificationengine.admin.notification.dto.response.AdminNotificationDetailResponse;
import com.notificationengine.admin.notification.dto.response.AdminNotificationPageResponse;
import com.notificationengine.admin.notification.service.AdminNotificationService;
import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {
    private final AdminNotificationService adminNotificationService;

    public AdminNotificationController(AdminNotificationService adminNotificationService) {
        this.adminNotificationService = adminNotificationService;
    }

    @GetMapping
    public ResponseEntity<AdminNotificationPageResponse> getNotifications(
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

        return ResponseEntity.ok(
                adminNotificationService.getNotifications( status,
                        channel,
                        pageable)
        );
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<AdminNotificationDetailResponse> getNotification(
            @PathVariable UUID notificationId
    ) {

        return ResponseEntity.ok(
                adminNotificationService.getNotification(notificationId)
        );
    }

    @GetMapping("/{notificationId}/attempts")
    public ResponseEntity<List<AdminNotificationAttemptResponse>> getNotificationAttempts(
            @PathVariable UUID notificationId
    ) {

        return ResponseEntity.ok(
                adminNotificationService
                        .getNotificationAttempts(notificationId)
        );
    }
}
