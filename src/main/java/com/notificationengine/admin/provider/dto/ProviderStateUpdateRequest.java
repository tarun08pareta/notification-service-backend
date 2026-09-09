package com.notificationengine.admin.provider.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderStateUpdateRequest {
    @NotNull
    private Boolean enabled;

    @NotNull
    @Min(0)
    private Integer priority;
//    public ProviderStateUpdateRequest() {
//    }

//    public Boolean getEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(Boolean enabled) {
//        this.enabled = enabled;
//    }
}
