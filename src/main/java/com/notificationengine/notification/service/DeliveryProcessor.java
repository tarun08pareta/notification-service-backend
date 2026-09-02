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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
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

    public void process(Notification notification) {

        notification.setStatus(NotificationStatus.PROCESSING);
        notificationRepository.save(notification);

        List<NotificationProvider> providers =
                providerRouter.route(notification);

        if (providers.isEmpty()) {
            markFailed(notification, "NO_PROVIDER", "No eligible provider found");
            return;
        }

        Optional<DeliveryAttempt> latestAttempt =
                deliveryAttemptRepository
                        .findTopByNotificationIdOrderByAttemptNumberDesc(
                                notification.getId()
                        );

        NotificationProvider provider =
                selectProvider(providers, latestAttempt);

        processSingleAttempt(notification, provider,providers);
    }

    private NotificationProvider selectProvider(
            List<NotificationProvider> providers,
            Optional<DeliveryAttempt> latestAttempt
    ) {
        if (latestAttempt.isEmpty()) {
            return providers.get(0);
        }

        String lastProvider = latestAttempt.get().getProvider();

        return providers.stream()
                .filter(provider -> provider.name().equals(lastProvider))
                .findFirst()
                .orElse(providers.get(0));
    }

    private void processSingleAttempt(
            Notification notification,
            NotificationProvider provider,
            List<NotificationProvider> providers
    ) {

        int attemptNumber =
                deliveryAttemptRepository
                        .findTopByNotificationIdOrderByAttemptNumberDesc(
                                notification.getId()
                        )
                        .map(attempt -> attempt.getAttemptNumber() + 1)
                        .orElse(1);

        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.setNotification(notification);
        attempt.setProvider(provider.name());
        attempt.setAttemptNumber(attemptNumber);
        attempt.setStatus(DeliveryAttemptStatus.PROCESSING);
        attempt.setStartedAt(OffsetDateTime.now());

        attempt = deliveryAttemptRepository.save(attempt);

        ProviderResult result;

        try {
            result = provider.send(notification);
        } catch (Exception ex) {
            result = ProviderResult.transientFailure(
                    "PROVIDER_EXCEPTION",
                    ex.getMessage()
            );
        }

        attempt.setCompletedAt(OffsetDateTime.now());

        if (result.success()) {
            handleSuccess(notification, attempt, result);
            return;
        }

        handleFailure(
                notification,
                attempt,
                provider,
                result,
                providers
        );
    }

    private void handleSuccess(
            Notification notification,
            DeliveryAttempt attempt,
            ProviderResult result
    ) {

        attempt.setStatus(DeliveryAttemptStatus.SUCCESS);
        attempt.setProviderMessageId(result.providerMessageId());

        deliveryAttemptRepository.save(attempt);

        notification.setStatus(NotificationStatus.SENT);
        notification.setRetryCount(0);
        notification.setNextRetryAt(null);

        notificationRepository.save(notification);

        log.info(
                "Notification {} delivered successfully using provider {}",
                notification.getId(),
                attempt.getProvider()
        );
    }

    private void handleFailure(
            Notification notification,
            DeliveryAttempt attempt,
            NotificationProvider provider,
            ProviderResult result,
            List<NotificationProvider> providers
    ) {

        attempt.setStatus(DeliveryAttemptStatus.FAILED);
        attempt.setErrorCode(result.errorCode());
        attempt.setErrorMessage(result.errorMessage());

        deliveryAttemptRepository.save(attempt);

        if (result.failureType() == ProviderFailureType.PERMANENT) {
            markFailed(
                    notification,
                    result.errorCode(),
                    result.errorMessage()
            );
            return;
        }

        handleTransientFailure(
                notification,
                provider,
                providers
        );
//        scheduleRetry(
//                notification,
//                result.failureType()
//        );
    }
    private void handleTransientFailure(
            Notification notification,
            NotificationProvider currentProvider,
            List<NotificationProvider> providers
    ) {

        int nextRetryNumber = notification.getRetryCount() + 1;

        if (retryPolicy.shouldRetry(
                ProviderFailureType.TRANSIENT,
                nextRetryNumber
        )) {

            notification.setRetryCount(nextRetryNumber);

            notification.setNextRetryAt(
                    OffsetDateTime.now()
                            .plus(retryPolicy.getDelay(nextRetryNumber))
            );

            notification.setStatus(NotificationStatus.RETRY_SCHEDULED);

            notificationRepository.save(notification);

            log.info(
                    "Notification {} scheduled for retry #{} using provider {} at {}",
                    notification.getId(),
                    nextRetryNumber,
                    currentProvider.name(),
                    notification.getNextRetryAt()
            );

            return;
        }

        Optional<NotificationProvider> nextProvider =
                getNextProvider(
                        providers,
                        currentProvider.name()
                );

        if (nextProvider.isEmpty()) {

            markFailed(
                    notification,
                    "ALL_PROVIDERS_FAILED",
                    "All eligible providers failed after retry exhaustion"
            );

            return;
        }

        notification.setRetryCount(0);
        notification.setNextRetryAt(null);
        notification.setStatus(NotificationStatus.PROCESSING);

        notificationRepository.save(notification);

        log.info(
                "Retry exhausted for provider {}. Falling back to provider {} for notification {}",
                currentProvider.name(),
                nextProvider.get().name(),
                notification.getId()
        );

        processSingleAttempt(
                notification,
                nextProvider.get(),
                providers
        );
    }

    private void scheduleRetry(
            Notification notification,
            ProviderFailureType failureType
    ) {

        int nextRetryNumber = notification.getRetryCount() + 1;

        if (!retryPolicy.shouldRetry(failureType, nextRetryNumber)) {
            markFailed(
                    notification,
                    "RETRY_EXHAUSTED",
                    "Maximum retry attempts exhausted"
            );
            return;
        }

        notification.setRetryCount(nextRetryNumber);

        notification.setNextRetryAt(
                OffsetDateTime.now()
                        .plus(retryPolicy.getDelay(nextRetryNumber))
        );

        notification.setStatus(NotificationStatus.RETRY_SCHEDULED);

        notificationRepository.save(notification);

        log.info(
                "Notification {} scheduled for retry #{} at {}",
                notification.getId(),
                nextRetryNumber,
                notification.getNextRetryAt()
        );
    }

    private void markFailed(
            Notification notification,
            String errorCode,
            String errorMessage
    ) {

        notification.setStatus(NotificationStatus.FAILED);
        notification.setNextRetryAt(null);

        notificationRepository.save(notification);

        log.error(
                "Notification {} failed. errorCode={}, errorMessage={}",
                notification.getId(),
                errorCode,
                errorMessage
        );
    }
    private int findProviderIndex(
            List<NotificationProvider> providers,
            String providerName
    ) {
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i).name().equals(providerName)) {
                return i;
            }
        }

        return -1;
    }

    private Optional<NotificationProvider> getNextProvider(
            List<NotificationProvider> providers,
            String currentProvider
    ) {
        int currentIndex = findProviderIndex(
                providers,
                currentProvider
        );

        if (currentIndex >= 0
                && currentIndex + 1 < providers.size()) {

            return Optional.of(
                    providers.get(currentIndex + 1)
            );
        }

        return Optional.empty();
    }

}