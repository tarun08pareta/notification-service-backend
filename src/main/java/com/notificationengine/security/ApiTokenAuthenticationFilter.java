package com.notificationengine.security;

import com.notificationengine.apiToken.domain.ApiToken;
import com.notificationengine.apiToken.repository.ApiTokenRepository;
import com.notificationengine.apiToken.service.ApiTokenHasher;
import com.notificationengine.apiToken.service.ApiTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiTokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
   //only allow for notification
   private static final String NOTIFICATION_ENDPOINT =
           "/api/v1/notifications";
    private final ApiTokenRepository apiTokenRepository;
    private final ApiTokenHasher apiTokenHasher;
    private final ApiTokenService apiTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // API tokens are intentionally allowed only for notification creation.
//        if (!"POST".equalsIgnoreCase(request.getMethod())
//                || !NOTIFICATION_ENDPOINT.equals(request.getRequestURI())) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
        //  API-key authentication is allowed only for notification creation.
        boolean notificationRequest =
                "POST".equalsIgnoreCase(
                        request.getMethod()
                )
                        && NOTIFICATION_ENDPOINT.equals(
                        request.getRequestURI()
                );

        if (!notificationRequest) {
            filterChain.doFilter(
                    request,
                    response
            );
            return;
        }
        String header = request.getHeader(API_KEY_HEADER);

        if (header == null || header.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        //  Reject requests that provide both JWT and API key credentials.
        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader != null
                && !authorizationHeader.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Use only one authentication mechanism per request"
            );

            return;
        }

        try {
            String tokenHash = apiTokenHasher.hash(header);
            ApiToken apiToken =
                    apiTokenRepository
                            .findByTokenHash(tokenHash)
                            .orElse(null);

            if (apiToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (apiToken.getRevokedAt() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            //  Treat a token as expired when expiresAt is equal to or before now.
            if (apiToken.getExpiresAt() != null
                    && !apiToken.getExpiresAt()
                    .isAfter(Instant.now())) {

                filterChain.doFilter(
                        request,
                        response
                );
                return;
            }
            apiTokenService.markAsUsed(apiToken);
            String username =
                    apiToken.getUser().getEmail();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            apiToken.getUser()
                                    .getRoles()
                                    .stream()
                                    .map(role ->
                                            new SimpleGrantedAuthority(
                                                    "ROLE_" + role.getName()
                                            )
                                    )
                                    .toList()
                    );
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            log.error(
                    "API token authentication failed for request {}",
                    request.getRequestURI(),
                    e
            );

            filterChain.doFilter(request, response);
        }
    }

}
