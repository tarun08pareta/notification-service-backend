package com.notificationengine.admin.roles.dto.request;

import com.notificationengine.user.domain.RoleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleStatusRequest {
    @NotNull
    RoleStatus status;
}
