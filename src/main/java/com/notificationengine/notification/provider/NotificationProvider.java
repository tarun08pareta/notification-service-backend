package com.notificationengine.notification.provider;

import com.notificationengine.notification.domain.Notification;

public interface NotificationProvider {
    String name();

    boolean support(Notification notification);

//    int priority();

    ProviderResult send(Notification notification);

}
