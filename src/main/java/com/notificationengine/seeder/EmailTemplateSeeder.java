package com.notificationengine.seeder;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplate;
import com.notificationengine.admin.emailtemplate.domain.EmailTemplateStatus;
import com.notificationengine.admin.emailtemplate.repository.EmailTemplateRepository;
import com.notificationengine.admin.emailtemplate.service.EmailTemplateVariableExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailTemplateSeeder implements CommandLineRunner {

    private final EmailTemplateRepository emailTemplateRepository;

    // UPDATED:
    // Use the same extractor for seeded templates and
    // admin-created/updated templates.
    private final EmailTemplateVariableExtractor emailTemplateVariableExtractor;

    @Override
    public void run(String... args) {

        seedWelcomeEmail();
        seedPasswordResetEmail();
        seedEmailVerificationEmail();
        seedPremiumPurchasedEmail();
        seedPaymentFailedEmail();
        seedPasswordChangedEmail();
    }

    /**
     * -------------------------------------------------------------------------
     * WELCOME EMAIL
     * -------------------------------------------------------------------------
     */
    private void seedWelcomeEmail() {

        if (emailTemplateRepository.existsByCode("WELCOME_EMAIL")) {
            return;
        }

        EmailTemplate template = new EmailTemplate();

        template.setCode("WELCOME_EMAIL");
        template.setName("Welcome Email");

        template.setSubject(
                "Welcome to {{companyName}}, {{firstName}}"
        );

        template.setHtmlBody(
                buildEmailHtml(
                        "Welcome",
                        """
                        <h2 style="
                            margin: 0 0 20px 0;
                            color: #111827;
                            font-size: 24px;
                            line-height: 1.3;
                        ">
                            Welcome, {{firstName}}!
                        </h2>

                        <p style="margin: 0 0 16px 0;">
                            Thank you for joining {{companyName}}.
                        </p>

                        <p style="margin: 0 0 16px 0;">
                            Your account has been successfully created.
                        </p>

                        <p style="margin: 0 0 16px 0;">
                            We are happy to have you with us.
                        </p>
                        """
                )
        );

        template.setTextBody("""
                Welcome, {{firstName}}!

                Thank you for joining {{companyName}}.

                Your account has been successfully created.

                We are happy to have you with us.

                Regards,
                {{companyName}}
                """);

        template.setStatus(EmailTemplateStatus.ACTIVE);
        template.setVersion(1);

        setVariables(template);

        emailTemplateRepository.save(template);
    }

    /**
     * -------------------------------------------------------------------------
     * PASSWORD RESET EMAIL
     * -------------------------------------------------------------------------
     */
    private void seedPasswordResetEmail() {

        if (emailTemplateRepository.existsByCode("PASSWORD_RESET")) {
            return;
        }

        EmailTemplate template = new EmailTemplate();

        template.setCode("PASSWORD_RESET");
        template.setName("Password Reset");

        template.setSubject("Reset your password");

        template.setHtmlBody(
                buildEmailHtml(
                        "Password Reset",
                        """
                        <h2 style="
                            margin: 0 0 20px 0;
                            color: #111827;
                            font-size: 24px;
                            line-height: 1.3;
                        ">
                            Password Reset
                        </h2>

                        <p style="margin: 0 0 16px 0;">
                            Hello {{firstName}},
                        </p>

                        <p style="margin: 0 0 16px 0;">
                            We received a request to reset your password.
                        </p>

                        <p style="margin: 0 0 24px 0;">
                            Click the button below to create a new password.
                        </p>

                        <p style="margin: 0 0 24px 0;">
                            <a
                                href="{{resetLink}}"
                                style="
                                    display: inline-block;
                                    padding: 12px 20px;
                                    background-color: #2563eb;
                                    color: #ffffff;
                                    text-decoration: none;
                                    border-radius: 6px;
                                    font-weight: 600;
                                "
                            >
                                Reset Password
                            </a>
                        </p>

                        <p style="margin: 0;">
                            If you did not request this, you can safely ignore
                            this email.
                        </p>
                        """
                )
        );

        template.setTextBody("""
                Password Reset

                Hello {{firstName}},

                We received a request to reset your password.

                Reset your password using this link:

                {{resetLink}}

                If you did not request this, you can safely ignore this email.

                Regards,
                {{companyName}}
                """);

        template.setStatus(EmailTemplateStatus.ACTIVE);
        template.setVersion(1);

        setVariables(template);

        emailTemplateRepository.save(template);
    }

    /**
     * -------------------------------------------------------------------------
     * EMAIL VERIFICATION
     * -------------------------------------------------------------------------
     */
    private void seedEmailVerificationEmail() {

        if (emailTemplateRepository.existsByCode("EMAIL_VERIFICATION")) {
            return;
        }

        EmailTemplate template = new EmailTemplate();

        template.setCode("EMAIL_VERIFICATION");
        template.setName("Email Verification");

        template.setSubject("Verify your email address");

        template.setHtmlBody(
                buildEmailHtml(
                        "Verify Email",
                        """
                        <h2 style="
                            margin: 0 0 20px 0;
                            color: #111827;
                            font-size: 24px;
                            line-height: 1.3;
                        ">
                            Verify Your Email Address
                        </h2>

                        <p style="margin: 0 0 16px 0;">
                            Hello {{firstName}},
                        </p>

                        <p style="margin: 0 0 24px 0;">
                            Please verify your email address to activate
                            your account.
                        </p>

                        <p style="margin: 0 0 24px 0;">
                            <a
                                href="{{verificationLink}}"
                                style="
                                    display: inline-block;
                                    padding: 12px 20px;
                                    background-color: #2563eb;
                                    color: #ffffff;
                                    text-decoration: none;
                                    border-radius: 6px;
                                    font-weight: 600;
                                "
                            >
                                Verify Email
                            </a>
                        </p>

                        <p style="margin: 0;">
                            If you did not create this account, you can safely
                            ignore this email.
                        </p>
                        """
                )
        );

        template.setTextBody("""
                Verify Your Email Address

                Hello {{firstName}},

                Please verify your email address to activate your account.

                Verification link:

                {{verificationLink}}

                If you did not create this account, you can safely ignore this email.

                Regards,
                {{companyName}}
                """);

        template.setStatus(EmailTemplateStatus.ACTIVE);
        template.setVersion(1);

        setVariables(template);

        emailTemplateRepository.save(template);
    }

    /**
     * -------------------------------------------------------------------------
     * PREMIUM PURCHASED EMAIL
     * -------------------------------------------------------------------------
     */
    private void seedPremiumPurchasedEmail() {

        if (emailTemplateRepository.existsByCode("PREMIUM_PURCHASED")) {
            return;
        }

        EmailTemplate template = new EmailTemplate();

        template.setCode("PREMIUM_PURCHASED");
        template.setName("Premium Purchased");

        template.setSubject(
                "Your premium subscription is active"
        );

        template.setHtmlBody(
                buildEmailHtml(
                        "Premium Subscription",
                        """
                        <h2 style="
                            margin: 0 0 20px 0;
                            color: #111827;
                            font-size: 24px;
                            line-height: 1.3;
                        ">
                            Premium Subscription Activated
                        </h2>

                        <p style="margin: 0 0 16px 0;">
                            Hello {{firstName}},
                        </p>

                        <p style="margin: 0 0 20px 0;">
                            Your premium subscription has been successfully
                            activated.
                        </p>

                        <div style="
                            margin: 0 0 24px 0;
                            padding: 16px;
                            background-color: #f9fafb;
                            border: 1px solid #e5e7eb;
                            border-radius: 8px;
                        ">

                            <p style="margin: 0 0 10px 0;">
                                <strong>Plan:</strong>
                                {{planName}}
                            </p>

                            <p style="margin: 0 0 10px 0;">
                                <strong>Amount:</strong>
                                {{amount}}
                            </p>

                            <p style="margin: 0;">
                                <strong>Transaction ID:</strong>
                                {{transactionId}}
                            </p>

                        </div>

                        <p style="margin: 0;">
                            Thank you for choosing {{companyName}}.
                        </p>
                        """
                )
        );

        template.setTextBody("""
                Premium Subscription Activated

                Hello {{firstName}},

                Your premium subscription has been successfully activated.

                Plan: {{planName}}

                Amount: {{amount}}

                Transaction ID: {{transactionId}}

                Thank you for choosing {{companyName}}.

                Regards,
                {{companyName}}
                """);

        template.setStatus(EmailTemplateStatus.ACTIVE);
        template.setVersion(1);

        setVariables(template);

        emailTemplateRepository.save(template);
    }

    /**
     * -------------------------------------------------------------------------
     * PAYMENT FAILED EMAIL
     * -------------------------------------------------------------------------
     */
    private void seedPaymentFailedEmail() {

        if (emailTemplateRepository.existsByCode("PAYMENT_FAILED")) {
            return;
        }

        EmailTemplate template = new EmailTemplate();

        template.setCode("PAYMENT_FAILED");
        template.setName("Payment Failed");

        template.setSubject(
                "Payment failed for your subscription"
        );

        template.setHtmlBody(
                buildEmailHtml(
                        "Payment Failed",
                        """
                        <h2 style="
                            margin: 0 0 20px 0;
                            color: #b91c1c;
                            font-size: 24px;
                            line-height: 1.3;
                        ">
                            Payment Failed
                        </h2>

                        <p style="margin: 0 0 16px 0;">
                            Hello {{firstName}},
                        </p>

                        <p style="margin: 0 0 20px 0;">
                            Unfortunately, we were unable to process
                            your payment.
                        </p>

                        <div style="
                            margin: 0 0 24px 0;
                            padding: 16px;
                            background-color: #fef2f2;
                            border: 1px solid #fecaca;
                            border-radius: 8px;
                        ">

                            <p style="margin: 0 0 10px 0;">
                                <strong>Amount:</strong>
                                {{amount}}
                            </p>

                            <p style="margin: 0;">
                                <strong>Transaction ID:</strong>
                                {{transactionId}}
                            </p>

                        </div>

                        <p style="margin: 0;">
                            Please update your payment method and try again.
                        </p>
                        """
                )
        );

        template.setTextBody("""
                Payment Failed

                Hello {{firstName}},

                Unfortunately, we were unable to process your payment.

                Amount: {{amount}}

                Transaction ID: {{transactionId}}

                Please update your payment method and try again.

                Regards,
                {{companyName}}
                """);

        template.setStatus(EmailTemplateStatus.ACTIVE);
        template.setVersion(1);

        setVariables(template);

        emailTemplateRepository.save(template);
    }

    /**
     * -------------------------------------------------------------------------
     * PASSWORD CHANGED EMAIL
     * -------------------------------------------------------------------------
     */
    private void seedPasswordChangedEmail() {

        if (emailTemplateRepository.existsByCode("PASSWORD_CHANGED")) {
            return;
        }

        EmailTemplate template = new EmailTemplate();

        template.setCode("PASSWORD_CHANGED");
        template.setName("Password Changed");

        template.setSubject(
                "Your password has been changed"
        );

        template.setHtmlBody(
                buildEmailHtml(
                        "Password Changed",
                        """
                        <h2 style="
                            margin: 0 0 20px 0;
                            color: #111827;
                            font-size: 24px;
                            line-height: 1.3;
                        ">
                            Password Changed Successfully
                        </h2>

                        <p style="margin: 0 0 16px 0;">
                            Hello {{firstName}},
                        </p>

                        <p style="margin: 0 0 16px 0;">
                            Your password for {{companyName}}
                            has been changed successfully.
                        </p>

                        <div style="
                            margin: 0 0 24px 0;
                            padding: 16px;
                            background-color: #f9fafb;
                            border: 1px solid #e5e7eb;
                            border-radius: 8px;
                        ">

                            <p style="
                                margin: 0;
                                color: #374151;
                            ">
                                If you did not make this change,
                                please contact support immediately.
                            </p>

                        </div>
                        """
                )
        );

        template.setTextBody("""
                Password Changed Successfully

                Hello {{firstName}},

                Your password for {{companyName}}
                has been changed successfully.

                If you did not make this change,
                please contact support immediately.

                Regards,
                {{companyName}}
                """);

        template.setStatus(EmailTemplateStatus.ACTIVE);
        template.setVersion(1);

        setVariables(template);

        emailTemplateRepository.save(template);
    }

    /**
     * -------------------------------------------------------------------------
     * COMMON EMAIL HTML LAYOUT
     * -------------------------------------------------------------------------
     *
     * Every seeded email contains:
     *
     * 1. Company logo
     * 2. Company name
     * 3. Template-specific content
     * 4. Reserved Advanced Variables placeholder
     * 5. Company footer
     */
    private String buildEmailHtml(
            String title,
            String content
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">

                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >

                    <title>%s</title>
                </head>

                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #f5f7fb;
                    font-family: Arial, Helvetica, sans-serif;
                ">

                    <div style="
                        max-width: 600px;
                        margin: 40px auto;
                        background-color: #ffffff;
                        border: 1px solid #e5e7eb;
                        border-radius: 12px;
                        overflow: hidden;
                    ">

                        <!-- Company Header -->

                        <div style="
                            padding: 24px 32px;
                            text-align: center;
                            border-bottom: 1px solid #e5e7eb;
                        ">

                            <img
                                src="{{logoUrl}}"
                                alt="{{companyName}}"
                                style="
                                    max-width: 140px;
                                    max-height: 60px;
                                    width: auto;
                                    height: auto;
                                    display: block;
                                    margin: 0 auto 12px auto;
                                    object-fit: contain;
                                "
                            />

                            <div style="
                                font-size: 18px;
                                line-height: 1.4;
                                font-weight: 600;
                                color: #111827;
                            ">
                                {{companyName}}
                            </div>

                        </div>

                        <!-- Main Content -->

                        <div style="
                            padding: 32px;
                            color: #374151;
                            font-size: 15px;
                            line-height: 1.7;
                        ">

                            %s

                        </div>

                        <!-- Advanced Variables -->

                        <div
                            data-template-section="advanced-variables"
                        >
                            {{advancedVariables}}
                        </div>

                        <!-- Footer -->

                        <div style="
                            padding: 20px 32px;
                            border-top: 1px solid #e5e7eb;
                            color: #6b7280;
                            font-size: 13px;
                            line-height: 1.6;
                        ">

                            Regards,<br>

                            <strong style="
                                color: #374151;
                            ">
                                {{companyName}}
                            </strong>

                        </div>

                    </div>

                </body>
                </html>
                """.formatted(
                title,
                content
        );
    }

    /**
     * -------------------------------------------------------------------------
     * VARIABLE EXTRACTION
     * -------------------------------------------------------------------------
     *
     * Uses the central extractor so seeded templates and
     * admin-created templates have identical variable metadata.
     *
     * IMPORTANT:
     *
     * {{advancedVariables}} is ignored by the extractor because
     * it is a reserved renderer placeholder.
     */
    private void setVariables(EmailTemplate template) {

        template.setVariables(
                emailTemplateVariableExtractor.extract(
                        template.getSubject(),
                        template.getHtmlBody(),
                        template.getTextBody()
                )
        );
    }
}