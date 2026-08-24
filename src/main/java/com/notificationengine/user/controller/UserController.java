package com.notificationengine.user.controller;

import com.notificationengine.user.dto.CreateUserRequest;
import com.notificationengine.user.dto.UserResponse;
import com.notificationengine.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userService;

    // Constructor injection keeps the controller loosely coupled and testable.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return userService.createUser(request);
    }
}
