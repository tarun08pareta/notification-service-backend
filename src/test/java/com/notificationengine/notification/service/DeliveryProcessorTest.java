package com.notificationengine.notification.service;

import com.notificationengine.notification.domain.DeliveryAttempt;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderResult;
import com.notificationengine.notification.provider.ProviderRouter;
import com.notificationengine.notification.repository.DeliveryAttemptRepository;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.notification.retry.RetryPolicy;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
@Disabled
public class DeliveryProcessorTest {
    private final ProviderRouter providerRouter = mock(ProviderRouter.class);
    private final DeliveryAttemptRepository deliveryAttemptRepository =
            mock(DeliveryAttemptRepository.class);
    private final NotificationRepository notificationRepository =
            mock(NotificationRepository.class);
    private final RetryPolicy retryPolicy = mock(RetryPolicy.class);
    private final MeterRegistry meterRegistry = mock(MeterRegistry.class);
    private final DeliveryProcessor deliveryProcessor =
            new DeliveryProcessor(
                    providerRouter,
                    deliveryAttemptRepository,
                    notificationRepository,
                    retryPolicy,
                    meterRegistry
            );

    @Test
    void shouldMarkNotificationAsSentWhenProviderSucceeds() {

        Notification notification = new Notification();

        NotificationProvider provider = mock(NotificationProvider.class);

        when(provider.name()).thenReturn("mailpit-email");

        when(providerRouter.route(notification))
                .thenReturn(List.of(provider));

        when(provider.send(notification))
                .thenReturn(
                        ProviderResult.success("provider-message-123")
                );

        deliveryProcessor.process(notification);

        verify(provider).send(notification);

        verify(deliveryAttemptRepository, times(2))
                .save(any(DeliveryAttempt.class));

        verify(notificationRepository, times(2))
                .save(notification);

        assert notification.getStatus() == NotificationStatus.SENT;
    }
}
