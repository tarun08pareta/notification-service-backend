package com.notificationengine.company.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "company_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_company_profiles_user_id",
                        columnNames = "user_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_company_profiles_user_id",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "logo_url", length = 2048)
    private String logoUrl;

    @Column(name = "logo_public_id", length = 255)
    private String logoPublicId;

    @Column(name = "sender_name", length = 150)
    private String senderName;

    @Column(name = "sender_email", length = 255)
    private String senderEmail;

    @Column(name = "reply_to_email", length = 255)
    private String replyToEmail;

    @Column(name = "sms_sender_id", length = 50)
    private String smsSenderId;

    @Column(name = "sms_sender_number", length = 50)
    private String smsSenderNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
