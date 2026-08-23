package com.notificationengine.client.service;

import com.notificationengine.client.domain.Client;
import com.notificationengine.client.domain.ClientStatus;
import com.notificationengine.client.dto.ClientResponse;
import com.notificationengine.client.dto.CreateClientRequest;
import com.notificationengine.client.mapper.ClientMapper;
import com.notificationengine.client.repository.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.net.CacheRequest;

@Service
public class ClientService {

    // Client database operations ke liye repository
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    // Repository dependency constructor ke through inject hogi
    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }
    
    public ClientResponse  createClient(CreateClientRequest request){
        Client client = new Client();
        client.setName(request.getName());
        client.setSlug(request.getSlug());
        client.setEmail(request.getEmail());
        client.setStatus(ClientStatus.ACTIVE);
        ClientResponse response = clientMapper.toResponse(clientRepository.save(client));
        return response;
    }
}
