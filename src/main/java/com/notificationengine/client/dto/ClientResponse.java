package com.notificationengine.client.dto;

import com.notificationengine.client.domain.ClientStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ClientResponse {
    private UUID id;
    private String name;
    private String slug;
    private String email;
    private ClientStatus status;
    private Instant createdAt;
    private Instant updatedAt;

}
