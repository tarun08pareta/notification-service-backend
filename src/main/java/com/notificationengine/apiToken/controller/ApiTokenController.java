package com.notificationengine.apiToken.controller;

import com.notificationengine.apiToken.dto.request.ApiTokenCreateRequest;
import com.notificationengine.apiToken.dto.response.ApiTokenCreateResponse;
import com.notificationengine.apiToken.dto.response.ApiTokenListResponse;
import com.notificationengine.apiToken.service.ApiTokenService;
import com.notificationengine.security.AuthenticatedUserService;
import com.notificationengine.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/api-tokens")
@RequiredArgsConstructor
public class ApiTokenController {
    private final ApiTokenService apiTokenService;
    private final AuthenticatedUserService authenticatedUserService;

    @PostMapping
    public ResponseEntity<ApiTokenCreateResponse> createToken(
            @Valid @RequestBody ApiTokenCreateRequest request
            ){
        User currentUser = authenticatedUserService
                .getCurrentUser();
        ApiTokenCreateResponse tokenResponse = apiTokenService.createToken(
                currentUser,
                request
        );

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping
    public ResponseEntity<List<ApiTokenListResponse>> getTokens() {

        // UPDATED: List only tokens belonging to the authenticated user.
        User currentUser =
                authenticatedUserService.getCurrentUser();

        List<ApiTokenListResponse> response =
                apiTokenService.getUserTokens(
                        currentUser.getId()
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tokenId}")
    public ResponseEntity<Void> revokeToken(
            @PathVariable java.util.UUID tokenId
    ) {

        // UPDATED: Revoke only a token owned by the authenticated user.
        User currentUser =
                authenticatedUserService.getCurrentUser();

        apiTokenService.revokeToken(
                tokenId,
                currentUser.getId()
        );

        return ResponseEntity.noContent().build();
    }

}
