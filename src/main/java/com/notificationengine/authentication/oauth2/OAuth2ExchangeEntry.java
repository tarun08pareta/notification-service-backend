package com.notificationengine.authentication.oauth2;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable value object representing one in-flight OAuth2 authorization
 * exchange entry.
 *
 * The entry is created when GoogleOAuth2SuccessHandler completes successfully
 * and is consumed exactly once by the /api/v1/auth/google/exchange endpoint.
 *
 * No JPA entity or DB migration is required — codes are short-lived (60 s)
 * and held in an in-memory ConcurrentHashMap.  If the server restarts the
 * user simply clicks "Continue with Google" again.
 */
public class OAuth2ExchangeEntry {

    private final UUID userId;
    private final Instant expiresAt;

    /** Monotonically set to true the first time the code is exchanged. */
    private volatile boolean consumed = false;

    public OAuth2ExchangeEntry(UUID userId, Instant expiresAt) {
        this.userId    = userId;
        this.expiresAt = expiresAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isConsumed() {
        return consumed;
    }

    /** Marks the entry as consumed. Returns true only on the first call. */
    public synchronized boolean consume() {
        if (consumed) {
            return false;
        }
        consumed = true;
        return true;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
