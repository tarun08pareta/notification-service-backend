package com.notificationengine.user.mapper;

import com.notificationengine.user.domain.Role;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAuthProvider(user.getAuthProvider());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        // Entity ke Role objects ko simple role-name Set mein convert karte hain.
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        response.setRoles(roles);

        // Password intentionally map nahi karte.
        return response;
    }
}
