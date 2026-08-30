package com.notificationengine.notification.provider;

import com.notificationengine.notification.domain.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProviderRouter {

    private final List<NotificationProvider> providers;

    public ProviderRouter(List<NotificationProvider> providers) {
        this.providers = providers;
    }

    public NotificationProvider route(Notification notification) {

        return providers.stream()
                .filter(provider -> provider.support(notification))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No provider available for channel: "
                                        + notification.getChannel()
                        )
                );
    }
}