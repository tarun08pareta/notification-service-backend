package com.notificationengine.emailtemplate.controller;

import com.notificationengine.admin.emailtemplate.dto.response.EmailTemplatePageResponse;
import com.notificationengine.admin.emailtemplate.dto.response.EmailTemplateResponse;
import com.notificationengine.admin.emailtemplate.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/email-templates")
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @GetMapping
    public EmailTemplatePageResponse getTemplates(
            @PageableDefault(
                    size = 10,
                    sort = "updatedAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        return emailTemplateService.getActiveTemplates(pageable);
    }

    @GetMapping("/{templateId}")
    public EmailTemplateResponse getTemplate(
            @PathVariable UUID templateId
    ) {

        return emailTemplateService.getActiveTemplate(templateId);
    }
}