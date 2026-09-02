package com.notificationengine.notification.domain;

public enum NotificationStatus {
    QUEUED,
    PROCESSING,
    RETRY_SCHEDULED,
    SENT,
    FAILED
}
