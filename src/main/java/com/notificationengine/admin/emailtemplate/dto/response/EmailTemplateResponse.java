package com.notificationengine.admin.emailtemplate.dto.response;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplateStatus;
import com.notificationengine.admin.emailtemplate.domain.EmailTemplateVariable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateResponse {

    private UUID id;
    private String code;
    private String name;
    private String subject;
    private String htmlBody;
    private String textBody;
    private EmailTemplateStatus status;
    private List<EmailTemplateVariable> variables;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;

}