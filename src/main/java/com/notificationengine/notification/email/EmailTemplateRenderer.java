package com.notificationengine.notification.email;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplate;
import com.notificationengine.admin.emailtemplate.domain.EmailTemplateVariable;
import com.notificationengine.admin.emailtemplate.repository.EmailTemplateRepository;
import com.notificationengine.company.repository.CompanyProfileRepository;
import com.notificationengine.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EmailTemplateRenderer {

    private static final Pattern VARIABLE_PATTERN =
            Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.-]+)\\s*}}");

    // UPDATED:
    // Reserved placeholder used inside the email template HTML.
    // Its value comes from Notification.advancedVariables.
    private static final String ADVANCED_VARIABLES_PLACEHOLDER =
            "{{advancedVariables}}";

    private final EmailTemplateRepository emailTemplateRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public RenderedEmail render(Notification notification) {

        EmailTemplate template =
                emailTemplateRepository.findByCode(notification.getTemplate())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Email template not found: "
                                                + notification.getTemplate()
                                )
                        );

        /*
         * ---------------------------------------------------------------------
         * NORMAL TEMPLATE VARIABLES
         * ---------------------------------------------------------------------
         *
         * These values belong to the template's declared variables.
         *
         * Example:
         *
         * {
         *     "firstName": "Tarun"
         * }
         */
        Map<String, String> variables =
                new HashMap<>();

        if (notification.getVariables() != null) {
            variables.putAll(notification.getVariables());
        }

        /*
         * ---------------------------------------------------------------------
         * COMPANY PROFILE VARIABLES
         * ---------------------------------------------------------------------
         *
         * companyName and logoUrl are controlled by the backend.
         *
         * Therefore, even if a client sends these values manually,
         * the Company Profile value takes precedence.
         */
        companyProfileRepository
                .findByUserId(notification.getUserId())
                .ifPresent(profile -> {

                    if (profile.getCompanyName() != null) {
                        variables.put(
                                "companyName",
                                profile.getCompanyName()
                        );
                    }

                    if (profile.getLogoUrl() != null) {
                        variables.put(
                                "logoUrl",
                                profile.getLogoUrl()
                        );
                    }
                });

        /*
         * ---------------------------------------------------------------------
         * REQUIRED VARIABLE VALIDATION
         * ---------------------------------------------------------------------
         *
         * Only variables declared by the email template are validated here.
         *
         * Advanced Variables are intentionally NOT validated because they
         * are optional and can contain any number of key/value pairs.
         */
        validateRequiredVariables(
                template.getVariables(),
                variables
        );

        /*
         * ---------------------------------------------------------------------
         * ADVANCED VARIABLES
         * ---------------------------------------------------------------------
         *
         * UPDATED:
         *
         * Do NOT calculate Advanced Variables by comparing notification
         * variables with template variables.
         *
         * They are now stored separately inside Notification.
         */
        Map<String, String> advancedVariables =
                notification.getAdvancedVariables();

        /*
         * ---------------------------------------------------------------------
         * BUILD ADVANCED VARIABLES HTML
         * ---------------------------------------------------------------------
         */
        String advancedVariablesHtml =
                buildAdvancedVariablesHtml(
                        advancedVariables
                );

        /*
         * ---------------------------------------------------------------------
         * RENDER SUBJECT
         * ---------------------------------------------------------------------
         */
        String renderedSubject =
                replaceVariables(
                        template.getSubject(),
                        variables
                );

        /*
         * ---------------------------------------------------------------------
         * RENDER HTML BODY
         * ---------------------------------------------------------------------
         */
        String renderedHtmlBody =
                replaceVariables(
                        template.getHtmlBody(),
                        variables
                );

        /*
         * UPDATED:
         *
         * Replace the reserved Advanced Variables placeholder after
         * normal template variables have been rendered.
         */
        renderedHtmlBody =
                renderedHtmlBody.replace(
                        ADVANCED_VARIABLES_PLACEHOLDER,
                        advancedVariablesHtml
                );

        /*
         * ---------------------------------------------------------------------
         * RENDER TEXT BODY
         * ---------------------------------------------------------------------
         *
         * Normal template variables are rendered here.
         *
         * Advanced Variables are currently an HTML-specific section.
         */
        String renderedTextBody =
                replaceVariables(
                        template.getTextBody(),
                        variables
                );

        return new RenderedEmail(
                renderedSubject,
                renderedHtmlBody,
                renderedTextBody
        );
    }

    /**
     * -------------------------------------------------------------------------
     * REQUIRED VARIABLE VALIDATION
     * -------------------------------------------------------------------------
     *
     * Validates only variables declared by the selected EmailTemplate.
     *
     * Example:
     *
     * Template:
     *
     * firstName   -> required
     * companyName -> required
     * logoUrl     -> optional
     *
     * Then firstName and companyName must exist.
     */
    private void validateRequiredVariables(
            List<EmailTemplateVariable> templateVariables,
            Map<String, String> variables
    ) {

        if (templateVariables == null) {
            return;
        }

        for (EmailTemplateVariable variable :
                templateVariables) {

            if (!variable.isRequired()) {
                continue;
            }

            String value =
                    variables.get(variable.getKey());

            if (value == null || value.isBlank()) {

                throw new IllegalArgumentException(
                        "Required email template variable is missing: "
                                + variable.getKey()
                );
            }
        }
    }

    /**
     * -------------------------------------------------------------------------
     * ADVANCED VARIABLES HTML
     * -------------------------------------------------------------------------
     *
     * Converts Advanced Variables into an HTML table.
     *
     * Example input:
     *
     * orderId      = 12345
     * customerType = Premium
     *
     * Result:
     *
     * Additional Information
     *
     * orderId       | 12345
     * customerType  | Premium
     *
     * -------------------------------------------------------------------------
     */
    private String buildAdvancedVariablesHtml(
            Map<String, String> advancedVariables
    ) {

        if (advancedVariables == null
                || advancedVariables.isEmpty()) {

            return "";
        }

        StringBuilder html =
                new StringBuilder();

        html.append("""
                <div style="
                    margin: 24px 0;
                    font-family: Arial, sans-serif;
                ">

                    <h3 style="
                        margin: 0 0 12px 0;
                        font-size: 16px;
                        font-weight: 600;
                        color: #111827;
                    ">
                        Additional Information
                    </h3>

                    <table style="
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 14px;
                    ">
                """);

        for (Map.Entry<String, String> entry :
                advancedVariables.entrySet()) {

            html.append("""
                    <tr>
                        <td style="
                            padding: 8px;
                            border: 1px solid #ddd;
                            font-weight: 600;
                            vertical-align: top;
                        ">
                    """)
                    .append(
                            escapeHtml(entry.getKey())
                    )
                    .append("""
                        </td>

                        <td style="
                            padding: 8px;
                            border: 1px solid #ddd;
                            vertical-align: top;
                        ">
                    """)
                    .append(
                            escapeHtml(entry.getValue())
                    )
                    .append("""
                        </td>
                    </tr>
                    """);
        }

        html.append("""
                    </table>
                </div>
                """);

        return html.toString();
    }

    /**
     * -------------------------------------------------------------------------
     * HTML ESCAPING
     * -------------------------------------------------------------------------
     *
     * Advanced Variable keys and values are user-provided data.
     *
     * Escape them before inserting them into generated HTML.
     */
    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * -------------------------------------------------------------------------
     * NORMAL TEMPLATE VARIABLE REPLACEMENT
     * -------------------------------------------------------------------------
     *
     * Replaces placeholders such as:
     *
     * {{firstName}}
     * {{companyName}}
     * {{logoUrl}}
     *
     * If a value is not available, the original placeholder is preserved.
     */
    private String replaceVariables(
            String content,
            Map<String, String> variables
    ) {

        if (content == null || content.isBlank()) {
            return content;
        }

        Matcher matcher =
                VARIABLE_PATTERN.matcher(content);

        StringBuffer result =
                new StringBuffer();

        while (matcher.find()) {

            String key =
                    matcher.group(1);

            String value =
                    variables.get(key);

            /*
             * Keep the original placeholder when a value is unavailable.
             *
             * Required variables are already validated separately.
             */
            if (value == null) {
                value = matcher.group(0);
            }

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(value)
            );
        }

        matcher.appendTail(result);

        return result.toString();
    }

    public record RenderedEmail(
            String subject,
            String htmlBody,
            String textBody
    ) {
    }
}