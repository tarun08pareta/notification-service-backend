package com.notificationengine.client.controller;

import com.notificationengine.client.dto.ClientResponse;
import com.notificationengine.client.dto.CreateClientRequest;
import com.notificationengine.client.dto.UpdateClientRequest;
import com.notificationengine.client.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse clientById(@PathVariable UUID id) {
        ClientResponse response = clientService.clientGetById(id);
        return response;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse updateClient(@PathVariable UUID id,
                                       @Valid @RequestBody UpdateClientRequest request){
        return clientService.updateClient(id, request);
    }
}
