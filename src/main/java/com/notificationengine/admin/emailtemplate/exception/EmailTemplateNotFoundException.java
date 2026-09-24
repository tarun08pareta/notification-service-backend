package com.notificationengine.admin.emailtemplate.exception;

import java.util.UUID;

public class EmailTemplateNotFoundException
        extends RuntimeException {

    public EmailTemplateNotFoundException(UUID id) {
        super("Email template not found: " + id);
    }
}