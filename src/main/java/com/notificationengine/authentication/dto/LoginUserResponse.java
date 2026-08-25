package com.notificationengine.authentication.dto;

import com.notificationengine.user.domain.User;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class LoginUserResponse {

    private final String id;
    private final String name;
    private final String email;
    private final Set<String> roles;
    private final String status;
    private final String authProvider;

    public LoginUserResponse(User user) {
        this.id = user.getId().toString();
        this.name = user.getName();
        this.email = user.getEmail();

        this.roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        this.status = user.getStatus().name();
        this.authProvider = user.getAuthProvider().name();
    }
}