package com.notificationengine.client.mapper;

import com.notificationengine.client.domain.Client;
import com.notificationengine.client.dto.ClientResponse;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public ClientResponse toResponse(Client client){
        ClientResponse response = new ClientResponse();

        response.setId(client.getId());
        response.setName(client.getName());
        response.setSlug(client.getSlug());
        response.setEmail(client.getEmail());
        response.setStatus(client.getStatus());
        response.setCreatedAt(client.getCreatedAt());
        response.setUpdatedAt(client.getUpdatedAt());
        return response;

    }
}
