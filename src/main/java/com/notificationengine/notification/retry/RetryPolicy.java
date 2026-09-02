package com.notificationengine.notification.retry;

import com.notificationengine.notification.provider.ProviderFailureType;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RetryPolicy {
    private final RetryProperties properties;

    public RetryPolicy(RetryProperties properties) {
        this.properties = properties;
    }


    public boolean shouldRetry(
            ProviderFailureType failureType,
            int retryNumber
    ){
        return failureType == ProviderFailureType.TRANSIENT
                && retryNumber <= properties.getMaxRetries();
    }

    public Duration getDelay(int retryNumber) {

        return Duration.ofMillis(
                properties.getInitialDelayMs()*
               ( 1L << (retryNumber - 1))
        );
    }
}
