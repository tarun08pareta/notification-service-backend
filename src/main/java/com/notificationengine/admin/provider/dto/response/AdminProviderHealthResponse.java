package com.notificationengine.admin.provider.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AdminProviderHealthResponse {
    private OffsetDateTime lastAttemptAt;
    private OffsetDateTime lastSuccessAt;
    private OffsetDateTime lastFailureAt;
    private String lastFailureCode;
    private String lastFailureMessage;

    public AdminProviderHealthResponse() {
    }

    public AdminProviderHealthResponse(
            OffsetDateTime lastAttemptAt,
            OffsetDateTime lastSuccessAt,
            OffsetDateTime lastFailureAt,
            String lastFailureCode,
            String lastFailureMessage
    ){
        this.lastAttemptAt = lastAttemptAt;
        this.lastSuccessAt = lastSuccessAt;
        this.lastFailureAt = lastFailureAt;
        this.lastFailureCode = lastFailureCode;
        this.lastFailureMessage = lastFailureMessage;
    }
}
