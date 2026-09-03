package com.notificationengine.notification.validation;


import com.notificationengine.notification.dto.NotificationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.InetAddress;
import java.util.regex.Pattern;

public class ChannelRecipientValidator
        implements ConstraintValidator<ChannelRecipient, NotificationRequest> {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+[1-9]\\d{7,14}$");

    @Override
    public boolean isValid(
            NotificationRequest request,
            ConstraintValidatorContext context
    ) {

        if (request == null) {
            return true;
        }

        if (request.getChannel() == null
                || request.getRecipient() == null
                || request.getRecipient().isBlank()) {
            return true;
        }

        return switch (request.getChannel()) {

            case EMAIL -> isValidEmail(request.getRecipient());

            case SMS -> PHONE_PATTERN.matcher(
                    request.getRecipient()
            ).matches();

            default -> true;
        };
    }

    private boolean isValidEmail(String recipient) {
        try {
            new jakarta.mail.internet.InternetAddress(recipient)
                    .validate();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}