package com.notificationengine.admin.provider.controller;


import com.notificationengine.admin.provider.dto.response.AdminProviderResponse;
import com.notificationengine.admin.provider.service.AdminProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/providers")
public class AdminProviderController {

    private final AdminProviderService adminProviderService;

    public AdminProviderController(
            AdminProviderService adminProviderService
    ) {
        this.adminProviderService = adminProviderService;
    }

    @GetMapping
    public ResponseEntity<List<AdminProviderResponse>> getProviders() {

        return ResponseEntity.ok(
                adminProviderService.getProviders()
        );
    }

}