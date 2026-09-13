package com.notificationengine.apiToken.repository;

import com.notificationengine.apiToken.domain.ApiToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiTokenRepository extends JpaRepository<ApiToken , UUID> {
     // Added token lookup for API-key authentication.
     @EntityGraph(attributePaths = {"user", "user.roles"})
     Optional<ApiToken> findByTokenHash(String tokenHash);

    Optional<ApiToken> findByIdAndUserId(UUID id,UUID userId);

    //  Return only active tokens for the authenticated user's token list.
    List<ApiToken> findAllByUserIdAndRevokedAtIsNullOrderByCreatedAtDesc(
            UUID userId
    );

    List<ApiToken> findAllByUserIdAndRevokedAtIsNullAndExpiresAtGreaterThanOrderByCreatedAtDesc(
            UUID userId,
            Instant now
    );

    //  Return only non-revoked and non-expired tokens for the user's active token list.
    @Query("""
            SELECT token
            FROM ApiToken token
            WHERE token.user.id = :userId
              AND token.revokedAt IS NULL
              AND (
                    token.expiresAt IS NULL
                    OR token.expiresAt > :now
                  )
            ORDER BY token.createdAt DESC
            """)
    List<ApiToken> findActiveTokensByUserId(
            @Param("userId") UUID userId,
            @Param("now") Instant now
    );

}
