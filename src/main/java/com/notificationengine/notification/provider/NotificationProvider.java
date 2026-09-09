package com.notificationengine.notification.provider;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationChannel;

import java.util.Set;

public interface NotificationProvider {
    String name();

    boolean support(Notification notification);

//    int priority();

    ProviderResult send(Notification notification);

    // Provider ke supported notification channels explicitly expose karta hai.
    Set<NotificationChannel> supportedChannels();
}
