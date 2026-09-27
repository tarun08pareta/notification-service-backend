package com.notificationengine.admin.roles.dto.response;

import com.notificationengine.user.domain.RoleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private UUID id;
    private String name;
    private RoleStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}