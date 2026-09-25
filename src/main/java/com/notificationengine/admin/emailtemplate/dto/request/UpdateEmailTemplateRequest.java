package com.notificationengine.admin.emailtemplate.dto.request;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplateVariable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailTemplateRequest {

    @NotBlank(message = "Template name is required")
    @Size(max = 150, message = "Template name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Template subject is required")
    @Size(max = 500, message = "Template subject must not exceed 500 characters")
    private String subject;

    @NotBlank(message = "HTML body is required")
    private String htmlBody;

    private String textBody;

    // UPDATED:
    private List<EmailTemplateVariable> variables;
}