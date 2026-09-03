package com.notificationengine.notification.provider.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "notification")
public class NotificationProviderProperties {
    private Map<String ,ProviderConfig> providers = new HashMap<>();

    @Getter
    @Setter
    public static  class ProviderConfig{
        private boolean enabled = true;
        private int priority;

    }
}
