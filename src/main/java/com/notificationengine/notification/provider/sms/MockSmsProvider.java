package com.notificationengine.notification.provider.sms;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockSmsProvider implements NotificationProvider {

    @Override
    public boolean support(Notification notification) {
        return notification.getChannel().name().equals("SMS");
    }

    @Override
    public ProviderResult send(Notification notification) {

        String recipient = notification.getRecipient();


        if (!isValidPhoneNumber(recipient)) {

            log.warn(
                    "Invalid SMS recipient: notificationId={}, provider={}",
                    notification.getId(),
                    name()
            );

            return ProviderResult.permanentFailure(
                    "INVALID_RECIPIENT",
                    "Recipient is not a valid phone number"
            );
        }

        String providerMessageId =
                "mock-sms-" + notification.getId();

        log.info(
                "Mock SMS delivered: notificationId={}, provider={}",
                notification.getId(),
                name()
        );

        return ProviderResult.success(providerMessageId);
    }

    private boolean isValidPhoneNumber(String recipient) {
        return recipient != null
                && recipient.matches("^\\+[1-9]\\d{7,14}$");
    }

    @Override
    public String name() {
        return "mock-sms";
    }
}