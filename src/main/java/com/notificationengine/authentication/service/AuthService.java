package com.notificationengine.authentication.service;

import com.notificationengine.authentication.dto.ChangePasswordRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public void changePassword(
            UUID userId,
            ChangePasswordRequest request
    ) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        // Google users do not have a local password.
        if (user.getPassword() == null) {
            throw new IllegalArgumentException(
                    "Password change is not available for Google authenticated users"
            );
        }

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }
}