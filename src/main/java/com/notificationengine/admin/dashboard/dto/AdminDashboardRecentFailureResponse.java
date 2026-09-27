package com.notificationengine.admin.dashboard.dto;

import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.domain.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminDashboardRecentFailureResponse {

    private UUID id;

    private NotificationChannel channel;

    private String recipient;

    private String template;

    private NotificationStatus status;

    private Integer retryCount;

    private Instant createdAt;
}