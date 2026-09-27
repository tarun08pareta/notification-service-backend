package com.notificationengine.admin.users.service;

import com.notificationengine.admin.users.dto.request.CreateAdminUserRequest;
import com.notificationengine.admin.users.dto.request.UpdateAdminUserRequest;
import com.notificationengine.user.domain.Role;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.domain.UserStatus;
import com.notificationengine.user.dto.AdminUserResponse;
import com.notificationengine.user.repository.RoleRepository;
import com.notificationengine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsers(
            String search,
            UserStatus status,
            String role,
            Pageable pageable
    ) {
        Page<User> users =
                userRepository.findAdminUsers(
                        normalize(search),
                        status,
                        normalize(role),
                        pageable
                );

        return users.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUser(UUID id) {

        User user = findUser(id);

        return toResponse(user);
    }

    public AdminUserResponse createUser(
            CreateAdminUserRequest request
    ) {

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException(
                    "User with this email already exists"
            );
        }

        Set<Role> roles = findRoles(request.getRoleIds());

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setAuthProvider(
                com.notificationengine.user.domain.AuthProvider.LOCAL
        );
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public AdminUserResponse updateUser(
            UUID id,
            UpdateAdminUserRequest request
    ) {

        User user = findUser(id);

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(
                request.getEmail(),
                id
        )) {
            throw new IllegalArgumentException(
                    "Another user already uses this email"
            );
        }

        Set<Role> roles = findRoles(request.getRoleIds());

        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setRoles(roles);

        return toResponse(userRepository.save(user));
    }

    public AdminUserResponse updateStatus(
            UUID id,
            UserStatus status
    ) {

        User user = findUser(id);

        user.setStatus(status);

        return toResponse(userRepository.save(user));
    }

    public void deleteUser(UUID id) {

        User user = findUser(id);

        user.getRoles().clear();

        userRepository.delete(user);
    }

    private User findUser(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + id
                        )
                );
    }

    private Set<Role> findRoles(Set<UUID> roleIds) {

        Set<Role> roles = new HashSet<>(
                roleRepository.findAllById(roleIds)
        );

        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException(
                    "One or more roles were not found"
            );
        }

        return roles;
    }

    private AdminUserResponse toResponse(User user) {

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAuthProvider(),
                user.getStatus(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .sorted()
                        .collect(Collectors.toList()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}