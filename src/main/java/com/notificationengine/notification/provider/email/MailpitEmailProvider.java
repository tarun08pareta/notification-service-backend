package com.notificationengine.notification.provider.email;

import com.notificationengine.company.domain.CompanyProfile;
import com.notificationengine.company.repository.CompanyProfileRepository;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationChannel;
import com.notificationengine.notification.email.EmailTemplateRenderer;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderResult;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Set;

@Slf4j
@Component
public class MailpitEmailProvider implements NotificationProvider {

    private final JavaMailSender mailSender;
    private final EmailTemplateRenderer emailTemplateRenderer;
    private final CompanyProfileRepository companyProfileRepository;

    public MailpitEmailProvider(JavaMailSender mailSender, EmailTemplateRenderer emailTemplateRenderer, CompanyProfileRepository companyProfileRepository) {
        this.mailSender = mailSender;
        this.emailTemplateRenderer = emailTemplateRenderer;
        this.companyProfileRepository = companyProfileRepository;
    }

    @Override
    public Set<NotificationChannel> supportedChannels() {
        return Set.of(NotificationChannel.EMAIL);
    }

    @Override
    public boolean support(Notification notification) {
        return notification.getChannel().name().equals("EMAIL");
    }

    @Override
    public ProviderResult send(Notification notification) {

//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//
//            message.setFrom("no-reply@notification-engine.local");
//            message.setTo(notification.getRecipient());
//            message.setSubject(notification.getTemplate());
//            message.setText(buildEmailBody(notification));
//
//            mailSender.send(message);
//
//            String providerMessageId =
//                    "mailpit-" + notification.getId();
//
//            log.info(
//                    "Email delivered to Mailpit: notificationId={}, provider={}",
//                    notification.getId(),
//                    name()
//            );
//
//            return ProviderResult.success(providerMessageId);
//
//        } catch (MailException exception) {
//
//            log.error(
//                    "Mailpit email delivery failed: notificationId={}, provider={}",
//                    notification.getId(),
//                    name(),
//                    exception
//            );
//
//            return ProviderResult.transientFailure(
//                    "MAIL_SEND_FAILED",
//                    "Unable to send email through Mailpit"
//            );
//        }

        try {
            // UPDATED: Resolve template + merge variables + render
            // subject, HTML body and text body.
            EmailTemplateRenderer.RenderedEmail renderedEmail =
                    emailTemplateRenderer.render(notification);

            MimeMessage mimeMessage =
                    mailSender.createMimeMessage();
            CompanyProfile companyProfile =
                    companyProfileRepository.findByUserId(
                            notification.getUserId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Company profile not found for user: "
                                            + notification.getUserId()
                            )
                    );

            MimeMessageHelper message =
                    new MimeMessageHelper(
                            mimeMessage,
                            true,
                            "UTF-8"
                    );

            // this is hardcode we need dynamcic

//            message.setFrom(
//                    "no-reply@notification-engine.local"
//            );

            // Sender email must come from Company Profile.
            if (companyProfile.getSenderEmail() == null
                    || companyProfile.getSenderEmail().isBlank()) {

                throw new IllegalArgumentException(
                        "Company sender email is not configured"
                );
            }

// UPDATED:
// Use Company Profile sender identity.
            if (companyProfile.getSenderName() != null
                    && !companyProfile.getSenderName().isBlank()) {

                message.setFrom(
                        new InternetAddress(
                                companyProfile.getSenderEmail(),
                                companyProfile.getSenderName()
                        )
                );

            }else {
                message.setFrom(
                        new InternetAddress(
                                companyProfile.getSenderEmail()
                        )
                );
            }
            // Reply-To is optional.
            if (companyProfile.getReplyToEmail() != null
                    && !companyProfile.getReplyToEmail().isBlank()) {

                message.setReplyTo(
                        companyProfile.getReplyToEmail()
                );
            }


            message.setTo(
                    notification.getRecipient()
            );

            // UPDATED: Use the subject from EmailTemplate.
            message.setSubject(
                    renderedEmail.subject()
            );

            // UPDATED: Send both HTML and plain-text versions.
//            message.setText(
//                    renderedEmail.textBody(),
//                    renderedEmail.htmlBody()
//            );
            String plainTextBody =
                    renderedEmail.textBody() == null
                            ? ""
                            : renderedEmail.textBody();

            message.setText(
                    plainTextBody,
                    renderedEmail.htmlBody()
            );

            mailSender.send(mimeMessage);

            String providerMessageId =
                    "mailpit-" + notification.getId();

            log.info(
                    "Email delivered to Mailpit: notificationId={}, provider={}",
                    notification.getId(),
                    name()
            );

            return ProviderResult.success(
                    providerMessageId
            );

        } catch (MessagingException exception) {

            log.error(
                    "Email message preparation failed: notificationId={}, provider={}",
                    notification.getId(),
                    name(),
                    exception
            );

            return ProviderResult.transientFailure(
                    "EMAIL_MESSAGE_BUILD_FAILED",
                    "Unable to prepare email message"
            );

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
        } catch (UnsupportedEncodingException exception) {

            log.error(
                    "Email message preparation failed: notificationId={}, provider={}",
                    notification.getId(),
                    name(),
                    exception
            );

            return ProviderResult.transientFailure(
                    "EMAIL_MESSAGE_BUILD_FAILED",
                    "Unable to prepare email message"
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