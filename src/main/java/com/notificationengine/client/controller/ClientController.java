package com.notificationengine.client.controller;

import com.notificationengine.client.domain.Client;
import com.notificationengine.client.dto.ClientResponse;
import com.notificationengine.client.dto.CreateClientRequest;
import com.notificationengine.client.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.CacheRequest;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {

    private final ClientService clientService;

    // Constructor injection
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse createClient(
            @Valid @RequestBody CreateClientRequest request
    ) {
        ClientResponse client = clientService.createClient(request);
        return client;
    }
}
