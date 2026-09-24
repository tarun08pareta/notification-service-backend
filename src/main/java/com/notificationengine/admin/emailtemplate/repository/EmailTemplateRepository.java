package com.notificationengine.admin.emailtemplate.repository;

import com.notificationengine.admin.emailtemplate.domain.EmailTemplate;
import com.notificationengine.admin.emailtemplate.domain.EmailTemplateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailTemplateRepository
        extends JpaRepository<EmailTemplate, UUID> {

    Optional<EmailTemplate> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    Page<EmailTemplate> findByStatus(
            EmailTemplateStatus status,
            Pageable pageable
    );

    Page<EmailTemplate> findAllByOrderByUpdatedAtDesc(
            Pageable pageable
    );
}