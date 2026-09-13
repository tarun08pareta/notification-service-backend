package com.notificationengine.apiToken.domain;

import com.notificationengine.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "api_tokens",
        indexes = {
                @Index(name = "idx_api_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_api_tokens_token_hash", columnList = "token_hash")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ApiToken {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(
                name = "user_id",
                nullable = false
        )
        private User user;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(name = "token_prefix", nullable = false, length = 30)
        private String tokenPrefix;

        @Column(name = "token_hash", nullable = false, unique = true, length = 64)
        private String tokenHash;

        @Column(name = "expires_at")
        private Instant expiresAt;

        @Column(name = "revoked_at")
        private Instant revokedAt;

        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @Column(name = "last_used_at")
        private Instant lastUsedAt;

        @PrePersist
        protected void onCreate() {
                createdAt = Instant.now();
        }
}
