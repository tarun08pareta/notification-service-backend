package com.notificationengine.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminDashboardStatisticsResponse {

    private long totalUsers;

    private long totalNotifications;

    private long sent;

    private long failed;

    private long queued;

    private long retryScheduled;
}