package com.notificationengine.notification.retry;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "notification.retry")
public class RetryProperties {
    private int maxRetries = 3;
    private long initialDelayMs = 1000;

}
