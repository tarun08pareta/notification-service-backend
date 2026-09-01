package com.notificationengine.notification.provider.email;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailpitEmailProvider implements NotificationProvider {

    private final JavaMailSender mailSender;

    public MailpitEmailProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean support(Notification notification) {
        return notification.getChannel().name().equals("EMAIL");
    }

    @Override
    public ProviderResult send(Notification notification) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("no-reply@notification-engine.local");
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getTemplate());
            message.setText(buildEmailBody(notification));

            mailSender.send(message);

            String providerMessageId =
                    "mailpit-" + notification.getId();

            log.info(
                    "Email delivered to Mailpit: notificationId={}, provider={}",
                    notification.getId(),
                    name()
            );

            return ProviderResult.success(providerMessageId);

        } catch (MailException exception) {

            log.error(
                    "Mailpit email delivery failed: notificationId={}, provider={}",
                    notification.getId(),
                    name(),
                    exception
            );

            return ProviderResult.transientFailure(
                    "MAIL_SEND_FAILED",
                    "Unable to send email through Mailpit"
            );
        }
    }

    private String buildEmailBody(Notification notification) {

        if (notification.getVariables() == null
                || notification.getVariables().isEmpty()) {
            return "Notification: " + notification.getTemplate();
        }

        StringBuilder body = new StringBuilder();

        body.append("Template: ")
                .append(notification.getTemplate())
                .append("\n\n");

        body.append("Variables:\n");

        notification.getVariables().forEach(
                (key, value) ->
                        body.append(key)
                                .append(": ")
                                .append(value)
                                .append("\n")
        );

        return body.toString();
    }

    @Override
    public String name() {
        return "mailpit-email";
    }
}