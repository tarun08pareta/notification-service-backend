package com.notificationengine.admin.emailtemplate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "email_templates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_email_templates_code",
                        columnNames = "code"
                )
        },
        indexes = {
                @Index(
                        name = "idx_email_templates_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_email_templates_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(name = "html_body", nullable = false, columnDefinition = "TEXT")
    private String htmlBody;

    @Column(name = "text_body", columnDefinition = "TEXT")
    private String textBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailTemplateStatus status;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = EmailTemplateStatus.ACTIVE;
        }

        if (version == null) {
            version = 1;
        }
        if (variables == null) {
            variables = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        if (variables == null) {
            variables = new ArrayList<>();
        }
    }

    //  Stores the variables required by this email template.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "variables",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private List<EmailTemplateVariable> variables = new ArrayList<>();
}