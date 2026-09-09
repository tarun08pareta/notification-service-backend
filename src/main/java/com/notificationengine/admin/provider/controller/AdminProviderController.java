package com.notificationengine.admin.provider.controller;


import com.notificationengine.admin.provider.domain.ProviderState;
import com.notificationengine.admin.provider.dto.ProviderPriorityUpdateRequest;
import com.notificationengine.admin.provider.dto.ProviderStateUpdateRequest;
import com.notificationengine.admin.provider.dto.response.AdminProviderResponse;
import com.notificationengine.admin.provider.service.AdminProviderService;
import com.notificationengine.notification.provider.NotificationProvider;
import com.notificationengine.notification.provider.config.NotificationProviderProperties;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/providers")
public class AdminProviderController {

    private final AdminProviderService adminProviderService;
    private final NotificationProviderProperties properties;
    private final List<NotificationProvider> providers;

    public AdminProviderController(
            AdminProviderService adminProviderService,
            NotificationProviderProperties properties,
            List<NotificationProvider> providers
    ) {
        this.adminProviderService = adminProviderService;
        this.properties = properties;
        this.providers = providers;
    }

    @GetMapping
    public ResponseEntity<List<AdminProviderResponse>> getProviders() {

        List<AdminProviderResponse> response =
                adminProviderService
                        .getAllProviderStates()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{providerName}/enabled")
    public ResponseEntity<AdminProviderResponse> updateEnabledState(
            @PathVariable String providerName,
            @Valid @RequestBody ProviderStateUpdateRequest request
    ) {

        ProviderState state =
                adminProviderService.updateEnabledState(
                        providerName,
                        request.getEnabled()
                );

        return ResponseEntity.ok(toResponse(state));
    }

    @PatchMapping("/{providerName}/priority")
    public ResponseEntity<AdminProviderResponse> updatePriority(
            @PathVariable String providerName,
            @Valid @RequestBody ProviderPriorityUpdateRequest request
    ) {

        ProviderState state =
                adminProviderService.updatePriority(
                        providerName,
                        request.getPriority()
                );

        return ResponseEntity.ok(toResponse(state));
    }


    private AdminProviderResponse toResponse(
            ProviderState state
    ) {


        NotificationProvider provider =
                providers.stream()
                        .filter(item ->
                                item.name().equals(
                                        state.getProvider()
                                )
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No provider implementation found for: "
                                                + state.getProvider()
                                )
                        );


        return new AdminProviderResponse(
                state.getProvider(),
                state.isEnabled(),
                state.getPriority(),
                provider.supportedChannels()
                        .stream()
                        .map(Enum::name)
                        .sorted()
                        .toList()
        );
    }
}