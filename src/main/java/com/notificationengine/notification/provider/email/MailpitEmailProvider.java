package com.notificationengine.notification.provider.email;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.NotificationProvider;
import org.springframework.stereotype.Component;

@Component
public class MailpitEmailProvider implements NotificationProvider {

    @Override
    public boolean support(Notification notification) {
        return notification.getChannel().name().equals("EMAIL");
    }
    @Override
    public void send(Notification notification) {

        // Mailpit integration will be implemented next.
    }
}
