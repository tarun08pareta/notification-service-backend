package com.notificationengine.admin.provider.service;

import com.notificationengine.admin.provider.dto.response.AdminProviderResponse;
import com.notificationengine.notification.provider.config.NotificationProviderProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminProviderService {
    private final NotificationProviderProperties properties;

    public AdminProviderService(
            NotificationProviderProperties properties
    ) {
        this.properties = properties;
    }


    public List<AdminProviderResponse> getProviders() {

        return properties.getProviders()
                .entrySet()
                .stream()
                .map(entry -> {

                    NotificationProviderProperties.ProviderConfig config =
                            entry.getValue();

                    return new AdminProviderResponse(
                            entry.getKey(),
                            config.isEnabled(),
                            config.getPriority()
                    );
                })
                .toList();
    }
}
