package com.notificationengine.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateClientRequest {
    @NotBlank
    @Size(max =150)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String slug;

    @Email
    @Size(max = 255)
    private String email;


}

