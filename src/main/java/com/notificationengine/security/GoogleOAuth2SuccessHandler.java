package com.notificationengine.security;

import com.notificationengine.authentication.oauth2.OAuth2ExchangeCodeStore;
import com.notificationengine.user.domain.AuthProvider;
import com.notificationengine.user.domain.Role;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.domain.UserStatus;
import com.notificationengine.user.repository.RoleRepository;
import com.notificationengine.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles a successful Google OAuth2 authentication.
 *
 * PREVIOUS BEHAVIOUR (broken):
 *   Wrote LoginResponse JSON directly to the HTTP response body.
 *   The browser rendered raw JSON because Angular had navigated away.
 *
 * FIXED BEHAVIOUR (popup flow):
 *   1. Find or create the application User.
 *   2. Generate a short-lived, single-use opaque exchange code via
 *      OAuth2ExchangeCodeStore — NOT the application JWT.
 *   3. Redirect the popup browser window to the Angular callback page:
 *        http://localhost:4200/auth/google/callback?code=<uuid>
 *   4. The Angular popup callback reads the code, posts it to the main
 *      window via postMessage, then closes.
 *   5. The main window POSTs the code to POST /api/v1/auth/google/exchange.
 *   6. That endpoint validates the code, generates the JWT, and returns
 *      LoginResponse — the JWT never appears in any URL.
 */
@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuth2ExchangeCodeStore exchangeCodeStore;

    /**
     * The Angular application's origin — used to build the popup redirect URL.
     * Override in application-prod.yaml for production deployments.
     */
    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email         = oauth2User.getAttribute("email");
        String name          = oauth2User.getAttribute("name");
        String providerUserId = oauth2User.getAttribute("sub");

        if (email == null || email.isBlank()) {
            // Google did not return an email — redirect with an error flag so
            // Angular can display a user-friendly message.
            response.sendRedirect(frontendUrl + "/auth/google/callback?error=email_unavailable");
            return;
        }

        // Find existing user or create a new Google-authenticated one.
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(name, email, providerUserId));

        // Generate a short-lived (60 s), single-use opaque code.
        // The code is a random UUID — it contains no user data or JWT material.
        String code = exchangeCodeStore.store(user.getId());

        // Redirect the popup to the Angular callback route.
        // The JWT is NOT in this URL — only the opaque exchange code.
        response.sendRedirect(frontendUrl + "/auth/google/callback?code=" + code);
    }

    private User createGoogleUser(String name, String email, String providerUserId) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new IllegalStateException("USER role not found in database"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(null);
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setProviderUserId(providerUserId);
        user.setStatus(UserStatus.ACTIVE);
        user.getRoles().add(userRole);

        return userRepository.save(user);
    }
}
