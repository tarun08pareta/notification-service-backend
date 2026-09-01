package com.notificationengine.notification.service;

import com.notificationengine.notification.domain.DeliveryAttempt;
import com.notificationengine.notification.domain.DeliveryAttemptStatus;
import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.ProviderFailureType;
import com.notificationengine.notification.provider.ProviderResult;
import com.notificationengine.notification.provider.ProviderRouter;
import com.notificationengine.notification.repository.DeliveryAttemptRepository;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.notification.retry.RetryPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DeliveryProcessor {

    private final ProviderRouter providerRouter;
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final NotificationRepository notificationRepository;
    private final RetryPolicy retryPolicy;

    public DeliveryProcessor(
            ProviderRouter providerRouter,
            DeliveryAttemptRepository deliveryAttemptRepository,
            NotificationRepository notificationRepository,
            RetryPolicy retryPolicy
    ) {
        this.providerRouter = providerRouter;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
        this.notificationRepository = notificationRepository;
        this.retryPolicy = retryPolicy;
    }

    @Transactional
    public void process(Notification notification) {

        notification.setStatus(NotificationStatus.PROCESSING);
        notificationRepository.save(notification);

        List<NotificationProvider> providers =
                providerRouter.route(notification);

        if (providers.isEmpty()) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
            return;
        }

        int attemptNumber = 1;

        for (NotificationProvider provider : providers) {

            int retryNumber = 0;

            while (true) {

                DeliveryAttempt attempt = new DeliveryAttempt();

                attempt.setNotification(notification);
                attempt.setProvider(provider.name());
                attempt.setAttemptNumber(attemptNumber++);
                attempt.setStatus(DeliveryAttemptStatus.PROCESSING);
                attempt.setStartedAt(OffsetDateTime.now());

                deliveryAttemptRepository.save(attempt);

                ProviderResult result = provider.send(notification);

                attempt.setCompletedAt(OffsetDateTime.now());

                if (result.success()) {

                    attempt.setStatus(DeliveryAttemptStatus.SUCCESS);
                    attempt.setProviderMessageId(
                            result.providerMessageId()
                    );

                    deliveryAttemptRepository.save(attempt);

                    notification.setStatus(NotificationStatus.SENT);
                    notificationRepository.save(notification);

                    return;
                }

                attempt.setStatus(DeliveryAttemptStatus.FAILED);
                attempt.setErrorCode(result.errorCode());
                attempt.setErrorMessage(result.errorMessage());

                deliveryAttemptRepository.save(attempt);

                if (result.failureType() ==
                        ProviderFailureType.PERMANENT) {

                    notification.setStatus(
                            NotificationStatus.FAILED
                    );

                    notificationRepository.save(notification);

                    return;
                }

                retryNumber++;

                if (!retryPolicy.shouldRetry(
                        result.failureType(),
                        retryNumber
                )) {
                    break;
                }

                Duration delay =
                        retryPolicy.getDelay(retryNumber);

                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException exception) {

                    Thread.currentThread().interrupt();

                    notification.setStatus(
                            NotificationStatus.FAILED
                    );

                    notificationRepository.save(notification);

                    return;
                }
            }
        }

        notification.setStatus(NotificationStatus.FAILED);
        notificationRepository.save(notification);
    }
}
