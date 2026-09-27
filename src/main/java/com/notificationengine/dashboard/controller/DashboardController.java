package com.notificationengine.dashboard.controller;

import com.notificationengine.dashboard.dto.UserDashboardResponse;
import com.notificationengine.dashboard.service.DashboardService;
import com.notificationengine.security.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    public ResponseEntity<UserDashboardResponse> getDashboard() {

        var user = authenticatedUserService.getCurrentUser();

        return ResponseEntity.ok(
                dashboardService.getUserDashboard(user.getId())
        );
    }
}