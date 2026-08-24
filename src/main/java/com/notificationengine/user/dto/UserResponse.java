package com.notificationengine.user.dto;

import com.notificationengine.user.domain.AuthProvider;
import com.notificationengine.user.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;

    private String name;

    private String email;

    private AuthProvider authProvider;

    private UserStatus status;

    private Set<String> roles;

    private Instant createdAt;

    private Instant updatedAt;
}