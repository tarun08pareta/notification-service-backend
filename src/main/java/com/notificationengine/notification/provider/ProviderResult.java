package com.notificationengine.notification.provider;

public record ProviderResult(
        boolean success,
        ProviderFailureType failureType,
        String providerMessageId,
        String errorCode,
        String errorMessage

) {

    public static ProviderResult success(
            String providerMessageId
    ) {
        return new ProviderResult(
                true,
                null,
                providerMessageId,
                null,
                null
        );
    }

    public static ProviderResult transientFailure(
            String errorCode,
            String errorMessage
    ) {
        return new ProviderResult(
                false,
                ProviderFailureType.TRANSIENT,
                null,
                errorCode,
                errorMessage
        );
    }

    public static ProviderResult permanentFailure(
            String errorCode,
            String errorMessage
    ) {
        return new ProviderResult(
                false,
                ProviderFailureType.PERMANENT,
                null,
                errorCode,
                errorMessage
        );
    }
}
