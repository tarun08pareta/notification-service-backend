package com.notificationengine.admin.emailtemplate.controller;

import com.notificationengine.admin.emailtemplate.dto.request.CreateEmailTemplateRequest;
import com.notificationengine.admin.emailtemplate.dto.request.UpdateEmailTemplateRequest;
import com.notificationengine.admin.emailtemplate.dto.request.UpdateEmailTemplateStatusRequest;
import com.notificationengine.admin.emailtemplate.dto.response.EmailTemplatePageResponse;
import com.notificationengine.admin.emailtemplate.dto.response.EmailTemplateResponse;
import com.notificationengine.admin.emailtemplate.service.EmailTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/email-templates")
@RequiredArgsConstructor
public class AdminEmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @GetMapping
    public ResponseEntity<EmailTemplatePageResponse> getTemplates(
            @PageableDefault(
                    size = 10,
                    sort = "updatedAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                emailTemplateService.getAdminTemplates(pageable)
        );
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<EmailTemplateResponse> getTemplate(
            @PathVariable UUID templateId
    ) {

        return ResponseEntity.ok(
                emailTemplateService.getAdminTemplate(templateId)
        );
    }

    @PostMapping
    public ResponseEntity<EmailTemplateResponse> createTemplate(
            @Valid @RequestBody CreateEmailTemplateRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        emailTemplateService.createTemplate(request)
                );
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<EmailTemplateResponse> updateTemplate(
            @PathVariable UUID templateId,
            @Valid @RequestBody UpdateEmailTemplateRequest request
    ) {

        return ResponseEntity.ok(
                emailTemplateService.updateTemplate(
                        templateId,
                        request
                )
        );
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable UUID templateId
    ) {

        emailTemplateService.deleteTemplate(templateId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{templateId}/status")
    public ResponseEntity<EmailTemplateResponse> updateStatus(
            @PathVariable UUID templateId,
            @Valid @RequestBody UpdateEmailTemplateStatusRequest request
    ) {

        return ResponseEntity.ok(
                emailTemplateService.updateStatus(
                        templateId,
                        request
                )
        );
    }
}
