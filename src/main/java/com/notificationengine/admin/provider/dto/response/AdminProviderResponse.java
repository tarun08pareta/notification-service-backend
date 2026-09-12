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

    private AdminProviderHealthResponse healthResponse;

    public AdminProviderResponse() {
    }

    public AdminProviderResponse(
            String name,
            boolean enabled,
            int priority,
            List<String> channels,
            AdminProviderHealthResponse healthResponse
    ) {
        this.name = name;
        this.enabled = enabled;
        this.priority = priority;
        this.channels =channels;
        this.healthResponse = healthResponse;
    }
}
