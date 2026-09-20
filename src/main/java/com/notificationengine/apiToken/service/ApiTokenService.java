package com.notificationengine.apiToken.service;

import com.notificationengine.apiToken.domain.ApiToken;
import com.notificationengine.apiToken.dto.request.ApiTokenCreateRequest;
import com.notificationengine.apiToken.dto.response.ApiTokenCreateResponse;
import com.notificationengine.apiToken.dto.response.ApiTokenListResponse;
import com.notificationengine.apiToken.repository.ApiTokenRepository;
import com.notificationengine.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiTokenService {
    private static final String TOKEN_PREFIX = "notification_engine";
    private static final int RANDOM_BYTES = 32;

    private final ApiTokenRepository apiTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    private final ApiTokenHasher apiTokenHasher;
    private final ApiTokenEncryptor apiTokenEncryptor;
    @Transactional
    public ApiTokenCreateResponse createToken(
            User user,
            ApiTokenCreateRequest request
    ){
     byte[] randomBytes = new byte[RANDOM_BYTES];
        secureRandom.nextBytes(randomBytes);
        String secret = HexFormat.of()
                .formatHex(randomBytes);

        String fullToken = TOKEN_PREFIX + secret;

//       has one way when user create then show only one time
//        String tokenHash = apiTokenHasher.hash(fullToken);

        // approch when user then show multiple time
        String encryptedToken = apiTokenEncryptor.encrypt(fullToken);
        String tokenPrefix = fullToken.substring(
                0,
                Math.min(fullToken.length(), 16)
        );

        ApiToken apiToken = new ApiToken();
        apiToken.setUser(user);
        apiToken.setName(request.getName());
        apiToken.setTokenPrefix(tokenPrefix);
//        apiToken.setTokenHash(tokenHash);
        apiToken.setTokenHash(encryptedToken);
        apiToken.setCreatedAt(Instant.now());

        ApiToken savedToken =
                apiTokenRepository.save(apiToken);

        return new ApiTokenCreateResponse(
                savedToken.getId(),
                savedToken.getName(),
                fullToken,
                savedToken.getCreatedAt(),
                savedToken.getExpiresAt()
        );
    }


    //token listing
    @Transactional(readOnly = true)
    public List<ApiTokenListResponse> getUserTokens(UUID userId){
        Instant now = Instant.now();
        List<ApiTokenListResponse> list = apiTokenRepository
//                .findActiveTokensByUserId(userId,now)   // not include revoked token
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(apiToken ->

                        // for hash

//                        new ApiTokenListResponse(
//                                apiToken.getId(),
//                                apiToken.getName(),
//                                apiToken.getTokenPrefix(),
//                                apiToken.getCreatedAt(),
//                                apiToken.getLastUsedAt(),
//                                apiToken.getExpiresAt(),
//                                apiToken.getRevokedAt()
//                        )

                        // for encrept or decrept
                        {
                            // Database se encrypted token nikal kar decrypt kar rahe hain
                            String decryptedFullToken = apiTokenEncryptor.decrypt(apiToken.getTokenHash());

                            return new ApiTokenListResponse(
                                    apiToken.getId(),
                                    apiToken.getName(),
                                    decryptedFullToken,
                                    apiToken.getTokenPrefix(),
                                    apiToken.getCreatedAt(),
                                    apiToken.getLastUsedAt(),
                                    apiToken.getExpiresAt(),
                                    apiToken.getRevokedAt()
                            );
                        }
                )
                .toList();
        return list;
    }

    @Transactional
    public void revokeToken(
            UUID tokenId,
            UUID userId
    ) {

        ApiToken token =
                apiTokenRepository
                        .findByIdAndUserId(tokenId, userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "API token not found"
                                )
                        );

        if (token.getRevokedAt() != null) {
            return;
        }

        token.setRevokedAt(Instant.now());

        apiTokenRepository.save(token);
    }

    @Transactional
    public void markAsUsed(ApiToken apiToken) {

        //  Track the last successful API-key usage time.
        apiToken.setLastUsedAt(Instant.now());

        apiTokenRepository.save(apiToken);
    }
}
