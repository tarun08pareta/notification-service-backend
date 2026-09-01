package com.notificationengine.notification.retry;

import com.notificationengine.notification.provider.ProviderFailureType;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RetryPolicy {
    private static final int MAX_RETRIES =3;
    private static final Duration INITIAL_DELAY=
            Duration.ofSeconds(1);

    public boolean shouldRetry(
            ProviderFailureType failureType,
            int retryNumber
    ){
        return failureType == ProviderFailureType.TRANSIENT
                && retryNumber <= MAX_RETRIES;
    }

    public Duration getDelay(int retryNumber) {

        return INITIAL_DELAY.multipliedBy(
                1L << (retryNumber - 1)
        );
    }
}
