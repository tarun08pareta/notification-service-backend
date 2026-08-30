package com.notificationengine.notification.mapper;

import com.notificationengine.notification.domain.Notification;
import com.notificationengine.notification.dto.NotificationRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationMapper {

    public Notification toEntity(
            NotificationRequest request,
            UUID userId
    ){
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setChannel(request.getChannel());
        notification.setTemplate(request.getTemplate());
        notification.setRecipient(request.getRecipient());
        notification.setVariables(request.getVariables());

        return notification;
    }
}
