package com.notificationengine.admin.emailtemplate.dto.request;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplateStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailTemplateStatusRequest {

    @NotNull(message = "Status is required")
    private EmailTemplateStatus status;

}