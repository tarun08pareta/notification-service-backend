package com.notificationengine.notification.provider.mock;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderResult;
import org.springframework.stereotype.Component;

@Component
public class MockTransientProvider implements NotificationProvider {

    @Override
    public String name() {
        return "mock-transient";
    }

    @Override
    public boolean support(Notification notification) {
        return true;
    }

    @Override
    public ProviderResult send(Notification notification) {

        return ProviderResult.transientFailure(
                "MOCK_TRANSIENT",
                "Simulated temporary provider failure"
        );
    }
}
