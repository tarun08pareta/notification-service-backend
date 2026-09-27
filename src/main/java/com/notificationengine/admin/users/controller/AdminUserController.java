package com.notificationengine.admin.users.controller;
import com.notificationengine.admin.users.dto.request.CreateAdminUserRequest;
import com.notificationengine.admin.users.dto.request.UpdateAdminUserRequest;
import com.notificationengine.admin.users.dto.request.UpdateUserStatusRequest;
import com.notificationengine.admin.users.service.AdminUserService;
import com.notificationengine.user.dto.AdminUserResponse;
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
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> listUsers(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            com.notificationengine.user.domain.UserStatus status,

            @RequestParam(required = false)
            String role,

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
                adminUserService.listUsers(
                        search,
                        status,
                        role,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getUser(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                adminUserService.getUser(id)
        );
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createUser(
            @Valid @RequestBody CreateAdminUserRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminUserService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAdminUserRequest request
    ) {

        return ResponseEntity.ok(
                adminUserService.updateUser(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminUserResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {

        return ResponseEntity.ok(
                adminUserService.updateStatus(
                        id,
                        request.getStatus()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id
    ) {

        adminUserService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}