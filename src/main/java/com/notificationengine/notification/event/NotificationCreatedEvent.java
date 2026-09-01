package com.notificationengine.notification.event;

import java.util.UUID;

public record NotificationCreatedEvent(UUID notificationId) {
}
