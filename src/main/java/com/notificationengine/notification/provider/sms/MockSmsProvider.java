package com.notificationengine.notification.provider.sms;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderResult;
import org.springframework.stereotype.Component;

@Component
public class MockSmsProvider implements NotificationProvider {

    @Override
    public boolean support(Notification notification) {
        return notification.getChannel().name().equals("SMS");
    }

    @Override
    public ProviderResult send(Notification notification) {

        // Mock SMS implementation will be implemented next.

        return ProviderResult.success(
                "mock-sms-" + notification.getId()
        );
    }
    @Override
    public String name() {
        return "mock-sms";
    }
}