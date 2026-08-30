package com.notificationengine.notification.dto.response;

import com.notificationengine.notification.domain.NotificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter

public class NotificationResponse {
    private UUID id;
    private NotificationStatus status;

    public NotificationResponse() {
    }

    public NotificationResponse(UUID id,
                                NotificationStatus status) {
        this.id = id;
        this.status = status;
    }
}
