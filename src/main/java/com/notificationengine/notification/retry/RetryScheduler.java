package com.notificationengine.notification.retry;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.domain.NotificationStatus;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.notification.service.DeliveryProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
public class RetryScheduler {
    private final NotificationRepository notificationRepository;
    private final DeliveryProcessor deliveryProcessor;

    public RetryScheduler(
            NotificationRepository notificationRepository,
            DeliveryProcessor deliveryProcessor
    ) {
        this.notificationRepository = notificationRepository;
        this.deliveryProcessor = deliveryProcessor;
    }

    @Scheduled(fixedDelay = 5000)
    public void processDueRetries(){
        OffsetDateTime now = OffsetDateTime.now();
        List<Notification> notifications = notificationRepository.findByStatusAndNextRetryAtLessThanEqual(
                NotificationStatus.RETRY_SCHEDULED,
                now
        );

        if(notifications.isEmpty()){
            return;
        }
        log.info("Found {} notification(s) ready for retry",
                notifications.size());

        for(Notification notification :notifications){
            int updateRows = notificationRepository.claimForProcessing(
                    notification.getId(),
                    NotificationStatus.PROCESSING,
                    NotificationStatus.RETRY_SCHEDULED
            );
            if (updateRows ==0){
                log.debug("Notification {} was already claimed by another worker",
                        notification.getId()
                );
                continue;
            }

            deliveryProcessor.process(notification);
        }
    }
}
