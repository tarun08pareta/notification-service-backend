package com.notificationengine.authentication.controller;

import com.notificationengine.authentication.dto.LoginRequest;
import com.notificationengine.authentication.dto.LoginResponse;
import com.notificationengine.authentication.dto.OAuth2ExchangeRequest;
import com.notificationengine.authentication.oauth2.OAuth2ExchangeCodeStore;
import com.notificationengine.authentication.oauth2.OAuth2ExchangeEntry;
import com.notificationengine.security.CustomUserDetailsService;
import com.notificationengine.security.JwtService;
import com.notificationengine.authentication.dto.LoginUserResponse;
import com.notificationengine.authentication.service.AuthService;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Dependencies for the Google OAuth2 exchange endpoint
    private final OAuth2ExchangeCodeStore exchangeCodeStore;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Existing email/password login — unchanged.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/google/login")
    public ResponseEntity<LoginResponse> exchangeGoogleCode(
            @Valid @RequestBody OAuth2ExchangeRequest request
    ) {
        // Atomically validate and consume the code.
        OAuth2ExchangeEntry entry = exchangeCodeStore.consume(request.getCode());

        if (entry == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Authorization code is invalid, expired, or has already been used"
            );
        }

        // Load the application User associated with this code.
        User user = userRepository.findById(entry.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Associated user account not found"
                ));

        // Load Spring Security UserDetails so the JWT subject is the user's email,
        // consistent with the email/password login flow.
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        Authentication applicationAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // Generate the application JWT using the existing JwtService — same as normal login.
        String accessToken = jwtService.generateToken(applicationAuthentication);

        LoginResponse loginResponse = new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationInSeconds(),
                new LoginUserResponse(user)
        );

        return ResponseEntity.ok(loginResponse);
    }
}
