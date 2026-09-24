package com.notificationengine.admin.emailtemplate.service;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplateVariable;
import com.notificationengine.admin.emailtemplate.domain.EmailTemplateVariableSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailTemplateVariableExtractor {

    private static final Pattern VARIABLE_PATTERN =
            Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.-]+)\\s*}}");

    // UPDATED:
    // Reserved placeholder used by the renderer to inject
    // the notification's advanced variables.
    private static final String ADVANCED_VARIABLES_PLACEHOLDER =
            "advancedVariables";

    public List<EmailTemplateVariable> extract(
            String subject,
            String htmlBody,
            String textBody
    ) {
        Map<String, EmailTemplateVariable> variables =
                new LinkedHashMap<>();

        extractFromText(subject, variables);
        extractFromText(htmlBody, variables);
        extractFromText(textBody, variables);

        return new ArrayList<>(variables.values());
    }

    private void extractFromText(
            String content,
            Map<String, EmailTemplateVariable> variables
    ) {
        if (content == null || content.isBlank()) {
            return;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(content);

        while (matcher.find()) {

            String key = matcher.group(1);

            // UPDATED:
            // advancedVariables is a reserved renderer placeholder.
            // It must NOT be stored inside email_templates.variables.
            if (ADVANCED_VARIABLES_PLACEHOLDER.equals(key)) {
                continue;
            }

            variables.putIfAbsent(
                    key,
                    new EmailTemplateVariable(
                            key,
                            resolveSource(key),
                            isRequired(key)
                    )
            );
        }
    }

    private EmailTemplateVariableSource resolveSource(String key) {

        return switch (key) {

            case "firstName",
                 "lastName",
                 "email",
                 "userId" ->
                    EmailTemplateVariableSource.USER;

            case "companyName",
                 "logoUrl",
                 "senderName",
                 "senderEmail",
                 "replyToEmail" ->
                    EmailTemplateVariableSource.COMPANY_PROFILE;

            case "planName",
                 "amount",
                 "transactionId",
                 "purchaseDate",
                 "paymentStatus" ->
                    EmailTemplateVariableSource.EVENT;

            default ->
                    EmailTemplateVariableSource.PLAYGROUND;
        };
    }

    private boolean isRequired(String key) {

        return switch (key) {

            case "logoUrl" ->
                    false;

            default ->
                    true;
        };
    }
}