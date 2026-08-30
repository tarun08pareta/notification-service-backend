package com.notificationengine.notification.dto;

import com.notificationengine.notification.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
public class NotificationRequest {

    @NotNull
    private NotificationChannel channel;
    @NotBlank
    private String recipient;
    @NotBlank
    private String template;
    private Map<String,String> variables;



}
