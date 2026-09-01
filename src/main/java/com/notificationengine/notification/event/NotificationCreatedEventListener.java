package com.notificationengine.notification.event;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.repository.NotificationRepository;
import com.notificationengine.notification.service.DeliveryProcessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationCreatedEventListener {
    private final NotificationRepository notificationRepository;
    private final DeliveryProcessor deliveryProcessor;

    public NotificationCreatedEventListener(
            NotificationRepository notificationRepository,
            DeliveryProcessor deliveryProcessor
    ) {
        this.notificationRepository = notificationRepository;
        this.deliveryProcessor = deliveryProcessor;
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationCreatedEvent event){
        Notification notification = notificationRepository.findById(event.notificationId())
                .orElseThrow(() ->
                        new IllegalStateException("Notification not found:" + event.notificationId()
                        )
                );
        deliveryProcessor.process(notification);
    }
}
