package com.notificationengine.notification.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ChannelRecipientValidator.class)
@Target({TYPE,ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ChannelRecipient {
    String message() default "Recipient is invalid for the selected channel";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
