package com.notificationengine.notification.provider;

import com.notificationengine.notification.domain.Notification;

public interface NotificationProvider {
    boolean support (Notification notification);
    void send(Notification notification);
}
