package com.notificationengine.admin.emailtemplate.service;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplate;
import com.notificationengine.admin.emailtemplate.domain.EmailTemplateStatus;
import com.notificationengine.admin.emailtemplate.dto.request.CreateEmailTemplateRequest;
import com.notificationengine.admin.emailtemplate.dto.request.UpdateEmailTemplateRequest;
import com.notificationengine.admin.emailtemplate.dto.request.UpdateEmailTemplateStatusRequest;
import com.notificationengine.admin.emailtemplate.dto.response.EmailTemplatePageResponse;
import com.notificationengine.admin.emailtemplate.dto.response.EmailTemplateResponse;
import com.notificationengine.admin.emailtemplate.exception.EmailTemplateCodeAlreadyExistsException;
import com.notificationengine.admin.emailtemplate.exception.EmailTemplateNotFoundException;
import com.notificationengine.admin.emailtemplate.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateVariableExtractor
            emailTemplateVariableExtractor;

    @Transactional(readOnly = true)
    public EmailTemplatePageResponse getAdminTemplates(
            Pageable pageable
    ) {

        Page<EmailTemplate> page =
                emailTemplateRepository.findAllByOrderByUpdatedAtDesc(
                        pageable
                );

        return toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public EmailTemplateResponse getAdminTemplate(
            UUID templateId
    ) {

        return toResponse(findTemplate(templateId));
    }

    @Transactional
    public EmailTemplateResponse createTemplate(
            CreateEmailTemplateRequest request
    ) {

        if (emailTemplateRepository.existsByCode(request.getCode())) {
            throw new EmailTemplateCodeAlreadyExistsException(
                    request.getCode()
            );
        }

        EmailTemplate template = new EmailTemplate();

        template.setCode(request.getCode().trim());
        template.setName(request.getName().trim());
        template.setSubject(request.getSubject().trim());
        template.setHtmlBody(request.getHtmlBody().trim());
        template.setTextBody(request.getTextBody().trim());
        template.setStatus(EmailTemplateStatus.ACTIVE);
        template.setVersion(1);
        template.setVariables(
                emailTemplateVariableExtractor.extract(
                        template.getSubject(),
                        template.getHtmlBody(),
                        template.getTextBody()
                )
        );

        try {
            EmailTemplate saved =
                    emailTemplateRepository.save(template);

            return toResponse(saved);

        } catch (DataIntegrityViolationException exception) {

            // UPDATED: Protects against duplicate codes created
            // concurrently by two requests.
            throw new EmailTemplateCodeAlreadyExistsException(
                    request.getCode()
            );
        }
    }

    @Transactional
    public EmailTemplateResponse updateTemplate(
            UUID templateId,
            UpdateEmailTemplateRequest request
    ) {

        EmailTemplate template = findTemplate(templateId);

        template.setName(request.getName().trim());
        template.setSubject(request.getSubject().trim());
        template.setHtmlBody(request.getHtmlBody());
        template.setTextBody(request.getTextBody());

        // UPDATED: Increment template version whenever content changes.
        template.setVersion(template.getVersion() + 1);

        template.setVariables(
                emailTemplateVariableExtractor.extract(
                        template.getSubject(),
                        template.getHtmlBody(),
                        template.getTextBody()
                )
        );

        EmailTemplate saved =
                emailTemplateRepository.save(template);

        return toResponse(saved);
    }

    @Transactional
    public void deleteTemplate(UUID templateId) {

        EmailTemplate template = findTemplate(templateId);

        emailTemplateRepository.delete(template);
    }

    @Transactional
    public EmailTemplateResponse updateStatus(
            UUID templateId,
            UpdateEmailTemplateStatusRequest request
    ) {

        EmailTemplate template = findTemplate(templateId);

        template.setStatus(request.getStatus());

        EmailTemplate saved =
                emailTemplateRepository.save(template);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EmailTemplatePageResponse getActiveTemplates(
            Pageable pageable
    ) {

        Page<EmailTemplate> page =
                emailTemplateRepository.findByStatus(
                        EmailTemplateStatus.ACTIVE,
                        pageable
                );

        return toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public EmailTemplateResponse getActiveTemplate(
            UUID templateId
    ) {

        EmailTemplate template =
                findTemplate(templateId);

        if (template.getStatus() != EmailTemplateStatus.ACTIVE) {
            throw new EmailTemplateNotFoundException(templateId);
        }

        return toResponse(template);
    }

    private EmailTemplate findTemplate(UUID templateId) {

        return emailTemplateRepository.findById(templateId)
                .orElseThrow(() ->
                        new EmailTemplateNotFoundException(templateId)
                );
    }

    private EmailTemplateResponse toResponse(
            EmailTemplate template
    ) {

        return new EmailTemplateResponse(
                template.getId(),
                template.getCode(),
                template.getName(),
                template.getSubject(),
                template.getHtmlBody(),
                template.getTextBody(),
                template.getStatus(),
                template.getVariables(),
                template.getVersion(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }

    private EmailTemplatePageResponse toPageResponse(
            Page<EmailTemplate> page
    ) {

        return new EmailTemplatePageResponse(
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}