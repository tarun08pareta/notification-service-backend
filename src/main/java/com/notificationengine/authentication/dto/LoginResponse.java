package com.notificationengine.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;
    private final LoginUserResponse user;

    public LoginResponse(
            String accessToken,
            String tokenType,
            long expiresIn,
            LoginUserResponse user
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
