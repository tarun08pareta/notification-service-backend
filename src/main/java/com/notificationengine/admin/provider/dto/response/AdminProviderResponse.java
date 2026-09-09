package com.notificationengine.admin.provider.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminProviderResponse {

    private String name;
    private boolean enabled;
    private List<String> channels;
    private int priority;

    public AdminProviderResponse() {
    }

    public AdminProviderResponse(
            String name,
            boolean enabled,
            int priority,
            List<String> channels
    ) {
        this.name = name;
        this.enabled = enabled;
        this.priority = priority;
        this.channels =channels;
    }
}
