package com.notificationengine.apiToken.repository;

import com.notificationengine.apiToken.domain.ApiToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

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


}
