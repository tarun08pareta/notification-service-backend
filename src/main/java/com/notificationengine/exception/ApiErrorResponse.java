package com.notificationengine.exception;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamo,
        int status,
        String error,
        String message,
        String path
) {
}
