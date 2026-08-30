package com.notificationengine.notification.provider.email;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderResult;
import org.springframework.stereotype.Component;

@Component
public class MailpitEmailProvider implements NotificationProvider {

    @Override
    public boolean support(Notification notification) {
        return notification.getChannel().name().equals("EMAIL");
    }
    @Override
    public ProviderResult send(Notification notification) {

        // Mailpit integration will be implemented next.

        return ProviderResult.success(
                "dev-mail-" + notification.getId()
        );
    }

    @Override
    public String name() {
        return "mailpit-email";
    }
}
