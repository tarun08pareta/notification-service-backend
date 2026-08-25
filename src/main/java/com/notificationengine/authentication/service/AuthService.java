package com.notificationengine.authentication.service;

import com.notificationengine.authentication.dto.LoginRequest;
import com.notificationengine.authentication.dto.LoginResponse;
import com.notificationengine.authentication.dto.LoginUserResponse;
import com.notificationengine.security.JwtService;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found")
                );

        String accessToken = jwtService.generateToken(authentication);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationInSeconds(),
                new LoginUserResponse(user)
        );
    }
}