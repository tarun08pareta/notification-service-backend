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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DeliveryProcessor {

    private final ProviderRouter providerRouter;
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final NotificationRepository notificationRepository;

    public DeliveryProcessor(
            ProviderRouter providerRouter,
            DeliveryAttemptRepository deliveryAttemptRepository,
            NotificationRepository notificationRepository
    ) {
        this.providerRouter = providerRouter;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void process(Notification notification){
        notification.setStatus(NotificationStatus.PROCESSING);
        notificationRepository.save(notification);

        List<NotificationProvider> providers =
                providerRouter.route(notification);

        if(providers.isEmpty()){
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);

            return;
        }

        int attemptNumber = 1;

        for (NotificationProvider provider : providers){
            DeliveryAttempt attempt = new DeliveryAttempt();
            attempt.setNotification(notification);
            attempt.setProvider(provider.name());
            attempt.setAttemptNumber(attemptNumber++);
            attempt.setStatus(DeliveryAttemptStatus.PROCESSING);
            attempt.setStartedAt(OffsetDateTime.now());
            deliveryAttemptRepository.save(attempt);

            ProviderResult send = provider.send(notification);
            attempt.setCompletedAt(OffsetDateTime.now());

            if (send.success()){
                attempt.setStatus(DeliveryAttemptStatus.SUCCESS);
                attempt.setProviderMessageId(
                        send.providerMessageId()
                );

                deliveryAttemptRepository.save(attempt);
                notification.setStatus(NotificationStatus.SENT);
                notificationRepository.save(notification);
                return;
            }
            attempt.setStatus(DeliveryAttemptStatus.FAILED);
            attempt.setErrorCode(send.errorCode());
            attempt.setErrorMessage(send.errorMessage());
            deliveryAttemptRepository.save(attempt);

            if (send.failureType()== ProviderFailureType.PERMANENT){
                notification.setStatus(NotificationStatus.FAILED);
                notificationRepository.save(notification);
                return;
            }
        }

        notification.setStatus(NotificationStatus.FAILED);
        notificationRepository.save(notification);
    }
}
