package com.notificationengine.notification.validation;


import com.notificationengine.notification.dto.NotificationRequest;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidChannelRecipientValidator
        implements ConstraintValidator<ValidChannelRecipient, NotificationRequest> {

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

        boolean valid = switch (request.getChannel()) {

            case EMAIL -> isValidEmail(request.getRecipient());

            case SMS -> PHONE_PATTERN
                    .matcher(request.getRecipient())
                    .matches();
        };

        if (!valid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                            "Recipient is invalid for channel "
                                    + request.getChannel()
                    )
                    .addPropertyNode("recipient")
                    .addConstraintViolation();
        }

        return valid;
    }

    private boolean isValidEmail(String recipient) {
        try {
            new InternetAddress(recipient).validate();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}