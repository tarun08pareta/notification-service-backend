package com.notificationengine.admin.dashboard.dto;

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
public class AdminDashboardResponse {

    private AdminDashboardStatisticsResponse statistics;

    private AdminDashboardChannelStatisticsResponse channels;

    private List<AdminDashboardProviderResponse> providers;

    private List<AdminDashboardRecentFailureResponse> recentFailures;
}