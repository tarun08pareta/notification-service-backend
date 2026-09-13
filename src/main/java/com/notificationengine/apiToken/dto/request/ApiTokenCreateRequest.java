package com.notificationengine.apiToken.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiTokenCreateRequest {

    @NotBlank(message = "Token Name is required")
    @Size(max = 100, message = "Token name cannot excees 100 characters")
    private String name;
}
