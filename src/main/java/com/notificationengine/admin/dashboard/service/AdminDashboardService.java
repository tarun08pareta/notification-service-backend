package com.notificationengine.admin.dashboard.service;

import com.notificationengine.admin.dashboard.dto.AdminDashboardAggregate;
import com.notificationengine.admin.dashboard.dto.AdminDashboardChannelStatisticsResponse;
import com.notificationengine.admin.dashboard.dto.AdminDashboardProviderResponse;
import com.notificationengine.admin.dashboard.dto.AdminDashboardRecentFailureResponse;
import com.notificationengine.admin.dashboard.dto.AdminDashboardResponse;
import com.notificationengine.admin.dashboard.dto.AdminDashboardStatisticsResponse;
import com.notificationengine.admin.provider.domain.ProviderState;
import com.notificationengine.admin.provider.service.AdminProviderService;
import com.notificationengine.notification.domain.DeliveryAttempt;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AdminProviderService adminProviderService;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {

        AdminDashboardAggregate aggregate =
                notificationRepository.getAdminDashboardAggregate();

        long totalUsers = userRepository.count();

        AdminDashboardStatisticsResponse statistics =
                new AdminDashboardStatisticsResponse(
                        totalUsers,
                        aggregate.getTotalNotifications(),
                        aggregate.getSent(),
                        aggregate.getFailed(),
                        aggregate.getQueued(),
                        aggregate.getRetryScheduled()
                );

        AdminDashboardChannelStatisticsResponse channels =
                new AdminDashboardChannelStatisticsResponse(
                        aggregate.getEmail(),
                        aggregate.getSms()
                );

        List<AdminDashboardProviderResponse> providers =
                adminProviderService
                        .getAllProviderStates()
                        .stream()
                        .map(this::toProviderResponse)
                        .toList();

        List<AdminDashboardRecentFailureResponse> recentFailures =
                notificationRepository
                        .findByStatusOrderByCreatedAtDesc(
                                NotificationStatus.FAILED,
                                PageRequest.of(0, 5)
                        )
                        .getContent()
                        .stream()
                        .map(this::toRecentFailureResponse)
                        .toList();

        return new AdminDashboardResponse(
                statistics,
                channels,
                providers,
                recentFailures
        );
    }

    private AdminDashboardProviderResponse toProviderResponse(
            ProviderState providerState
    ) {

        var health =
                adminProviderService.getProviderHealth(
                        providerState.getProvider()
                );

        return new AdminDashboardProviderResponse(
                providerState.getProvider(),
                providerState.isEnabled(),
                providerState.getPriority(),
                health.getLastAttemptAt(),
                health.getLastSuccessAt(),
                health.getLastFailureAt(),
                health.getLastFailureCode(),
                health.getLastFailureMessage()
        );
    }

    private AdminDashboardRecentFailureResponse toRecentFailureResponse(
            Notification notification
    ) {

        return new AdminDashboardRecentFailureResponse(
                notification.getId(),
                notification.getChannel(),
                notification.getRecipient(),
                notification.getTemplate(),
                notification.getStatus(),
                notification.getRetryCount(),
                notification.getCreatedAt()
        );
    }
}