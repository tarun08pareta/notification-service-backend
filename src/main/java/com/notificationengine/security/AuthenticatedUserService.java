package com.notificationengine.security;

import com.notificationengine.user.domain.User;
import com.notificationengine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        String name = authentication.getName();
        return userRepository.findByEmail(name)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));

    }
}
