package com.notificationengine.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminDashboardProviderResponse {

    private String provider;

    private boolean enabled;

    private int priority;

    private OffsetDateTime lastAttemptAt;

    private OffsetDateTime lastSuccessAt;

    private OffsetDateTime lastFailureAt;

    private String lastFailureCode;

    private String lastFailureMessage;
}