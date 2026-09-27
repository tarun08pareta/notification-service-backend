package com.notificationengine.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDashboardAggregate {

    private long totalNotifications;

    private long sent;

    private long failed;

    private long queued;

    private long retryScheduled;

    private long email;

    private long sms;
}