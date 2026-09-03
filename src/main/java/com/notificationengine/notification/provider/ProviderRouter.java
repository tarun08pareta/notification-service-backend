package com.notificationengine.notification.provider;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.config.NotificationProviderProperties;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProviderRouter {

    private final List<NotificationProvider> providers;
    private final NotificationProviderProperties properties;

    public ProviderRouter(
            List<NotificationProvider> providers,
            NotificationProviderProperties properties) {
        this.providers = providers;
        this.properties = properties;
    }

    public List<NotificationProvider> route(Notification notification) {

        return providers.stream()
                .filter(provider -> isEnabled(provider))
                .filter(provider -> provider.support(notification))
                .sorted(
                        Comparator.comparingInt(
                                provider -> getPriority(provider)
                        )
                )
                .toList();
//                .orElseThrow(() ->
//                        new IllegalStateException(
//                                "No provider available for channel: "
//                                        + notification.getChannel()
//                        )
//                );
    }
    private boolean isEnabled(NotificationProvider provider) {
        NotificationProviderProperties.ProviderConfig config =
                properties.getProviders().get(provider.name());

        if (config == null) {
            throw new IllegalStateException(
                    "No configuration found for provider: "
                            + provider.name()
            );
        }

        return config.isEnabled();
    }
    private int getPriority(NotificationProvider provider) {
        NotificationProviderProperties.ProviderConfig config =
                properties.getProviders().get(provider.name());

        if (config == null) {
            throw new IllegalStateException(
                    "No configuration found for provider: "
                            + provider.name()
            );
        }
        return config.getPriority();
    }
}