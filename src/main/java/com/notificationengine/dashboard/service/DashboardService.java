package com.notificationengine.dashboard.service;

import com.notificationengine.dashboard.dto.DashboardChannelStatisticsResponse;
import com.notificationengine.dashboard.dto.DashboardRecentNotificationResponse;
import com.notificationengine.dashboard.dto.DashboardStatisticsResponse;
import com.notificationengine.dashboard.dto.UserDashboardAggregate;
import com.notificationengine.dashboard.dto.UserDashboardResponse;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public UserDashboardResponse getUserDashboard(UUID userId) {

        UserDashboardAggregate aggregate =
                notificationRepository.getUserDashboardAggregate(userId);

        List<DashboardRecentNotificationResponse> recentNotifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                userId,
                                PageRequest.of(0, 5)
                        )
                        .getContent()
                        .stream()
                        .map(this::toRecentNotificationResponse)
                        .toList();

        DashboardStatisticsResponse statistics =
                new DashboardStatisticsResponse(
                        aggregate.getTotalNotifications(),
                        aggregate.getSent(),
                        aggregate.getFailed(),
                        aggregate.getQueued(),
                        aggregate.getRetryScheduled()
                );

        DashboardChannelStatisticsResponse channels =
                new DashboardChannelStatisticsResponse(
                        aggregate.getEmail(),
                        aggregate.getSms()
                );

        return new UserDashboardResponse(
                statistics,
                channels,
                recentNotifications
        );
    }

    private DashboardRecentNotificationResponse toRecentNotificationResponse(
            Notification notification
    ) {

        return new DashboardRecentNotificationResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getTemplate(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}