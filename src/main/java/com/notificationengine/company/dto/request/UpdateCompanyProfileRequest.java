package com.notificationengine.company.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCompanyProfileRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;

    @Size(max = 150, message = "Sender name must not exceed 150 characters")
    private String senderName;

    @Email(message = "Sender email must be valid")
    @Size(max = 255, message = "Sender email must not exceed 255 characters")
    private String senderEmail;

    @Email(message = "Reply-to email must be valid")
    @Size(max = 255, message = "Reply-to email must not exceed 255 characters")
    private String replyToEmail;

    @Size(max = 50, message = "SMS sender ID must not exceed 50 characters")
    private String smsSenderId;

    @Size(max = 50, message = "SMS sender number must not exceed 50 characters")
    private String smsSenderNumber;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;
}