package com.notificationengine.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateClientRequest {

    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name ;
    @Size(max = 100, message = "Slug must not exceed 100 characters")
    private String slug;
    @Email(message = "Email must be valid")
    private String email;


}
