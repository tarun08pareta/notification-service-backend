package com.notificationengine.admin.provider.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminProviderResponse {

    private String name;
    private boolean enabled;
    private int priority;

    public AdminProviderResponse() {
    }

    public AdminProviderResponse(
            String name,
            boolean enabled,
            int priority
    ) {
        this.name = name;
        this.enabled = enabled;
        this.priority = priority;
    }
}
