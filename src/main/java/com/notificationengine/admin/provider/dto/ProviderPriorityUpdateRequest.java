package com.notificationengine.admin.provider.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderPriorityUpdateRequest {
    @NotNull
    @Min(0)
    private Integer priority;
}
