package com.notificationengine.admin.emailtemplate.exception;

public class EmailTemplateCodeAlreadyExistsException
        extends RuntimeException {

    public EmailTemplateCodeAlreadyExistsException(String code) {
        super("Email template code already exists: " + code);
    }
}