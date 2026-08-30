package com.notificationengine.notification.provider.sms;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.NotificationProvider;
import org.springframework.stereotype.Component;

@Component
public class MockSmsProvider implements NotificationProvider {

    @Override
    public boolean support(Notification notification) {
        return notification.getChannel().name().equals("SMS");
    }

    @Override
    public void send(Notification notification) {

        // Mock SMS delivery will be implemented next.
    }
}