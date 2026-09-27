package com.notificationengine.user.dto; // Adjust package name as needed

import com.notificationengine.user.domain.AuthProvider;
import com.notificationengine.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private UUID id;
    private String name;
    private String email;
    private AuthProvider authProvider;
    private UserStatus status;
    private List<String> roles;
    private Instant createdAt;
    private Instant updatedAt;
}