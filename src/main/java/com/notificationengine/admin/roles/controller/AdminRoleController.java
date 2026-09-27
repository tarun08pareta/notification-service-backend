package com.notificationengine.admin.roles.controller;
import com.notificationengine.admin.roles.dto.request.CreateRoleRequest;
import com.notificationengine.admin.roles.dto.request.UpdateRoleRequest;
import com.notificationengine.admin.roles.dto.request.UpdateRoleStatusRequest;
import com.notificationengine.admin.roles.dto.response.RoleResponse;
import com.notificationengine.admin.roles.service.AdminRoleService;
import com.notificationengine.user.domain.RoleStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    @GetMapping
    public ResponseEntity<Page<RoleResponse>> listRoles(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            RoleStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction
    ) {

        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable =
                PageRequest.of(
                        page,
                        Math.min(size, 100),
                        Sort.by(sortDirection, sortBy)
                );

        return ResponseEntity.ok(
                adminRoleService.listRoles(
                        search,
                        status,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                adminRoleService.getRole(id)
        );
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(
            @Valid @RequestBody CreateRoleRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        adminRoleService.createRole(request)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {

        return ResponseEntity.ok(
                adminRoleService.updateRole(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RoleResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleStatusRequest request
    ) {

        return ResponseEntity.ok(
                adminRoleService.updateStatus(
                        id,
                        request.getStatus()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable UUID id
    ) {

        adminRoleService.deleteRole(id);

        return ResponseEntity.noContent().build();
    }
}