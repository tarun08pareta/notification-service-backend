package com.notificationengine.admin.roles.service;
import com.notificationengine.admin.roles.dto.request.CreateRoleRequest;
import com.notificationengine.admin.roles.dto.request.UpdateRoleRequest;
import com.notificationengine.admin.roles.dto.response.RoleResponse;
import com.notificationengine.user.domain.Role;
import com.notificationengine.user.domain.RoleStatus;
import com.notificationengine.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<RoleResponse> listRoles(
            String search,
            RoleStatus status,
            Pageable pageable
    ) {

        Page<Role> roles;

        if (status != null) {
            roles = roleRepository
                    .findByNameContainingIgnoreCaseAndStatus(
                            search == null ? "" : search.trim(),
                            status,
                            pageable
                    );
        } else {
            roles = roleRepository
                    .findAll(
                            pageable
                    );
        }

        return roles.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public RoleResponse getRole(UUID id) {

        return toResponse(findRole(id));
    }

    public RoleResponse createRole(
            CreateRoleRequest request
    ) {

        String name = normalizeName(request.getName());

        if (roleRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "Role already exists: " + name
            );
        }

        Role role = new Role();

        role.setName(name);
        role.setStatus(RoleStatus.ACTIVE);

        return toResponse(
                roleRepository.save(role)
        );
    }

    public RoleResponse updateRole(
            UUID id,
            UpdateRoleRequest request
    ) {

        Role role = findRole(id);

        String name = normalizeName(request.getName());

        if (roleRepository.existsByNameIgnoreCaseAndIdNot(
                name,
                id
        )) {
            throw new IllegalArgumentException(
                    "Role already exists: " + name
            );
        }

        role.setName(name);

        return toResponse(
                roleRepository.save(role)
        );
    }

    public RoleResponse updateStatus(
            UUID id,
            RoleStatus status
    ) {

        Role role = findRole(id);

        role.setStatus(status);

        return toResponse(
                roleRepository.save(role)
        );
    }

    public void deleteRole(UUID id) {

        Role role = findRole(id);

        roleRepository.delete(role);
    }

    private Role findRole(UUID id) {

        return roleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + id
                        )
                );
    }

    private RoleResponse toResponse(Role role) {

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getStatus(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    private String normalizeName(String name) {

        return name.trim().toUpperCase();
    }
}