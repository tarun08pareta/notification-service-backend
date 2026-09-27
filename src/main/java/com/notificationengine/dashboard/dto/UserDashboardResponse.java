package com.notificationengine.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDashboardResponse {

    private DashboardStatisticsResponse statistics;

    private DashboardChannelStatisticsResponse channels;

    private List<DashboardRecentNotificationResponse> recentNotifications;
}