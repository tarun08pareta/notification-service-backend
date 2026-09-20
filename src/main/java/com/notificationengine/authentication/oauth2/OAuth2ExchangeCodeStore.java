package com.notificationengine.authentication.oauth2;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, in-memory store for short-lived OAuth2 exchange codes.
 *
 * Lifecycle:
 *   1. GoogleOAuth2SuccessHandler calls store() after successful Google auth.
 *   2. The code is included in the redirect URL to the Angular popup callback.
 *   3. Angular popup posts the code to the main window via postMessage.
 *   4. The main window POSTs the code to /api/v1/auth/google/exchange.
 *   5. The exchange endpoint calls consume(), which atomically marks the
 *      entry as used and returns it.
 *
 * Stale-entry eviction is opportunistic: expired entries are removed whenever
 * store() is called.  For a low-traffic dev/staging environment this is
 * sufficient.  Production deployments that require persistence across restarts
 * or multi-instance deployments should use Redis instead.
 */
@Component
public class OAuth2ExchangeCodeStore {

    /** TTL for each exchange code. */
    private static final long CODE_TTL_SECONDS = 60L;

    private final ConcurrentHashMap<String, OAuth2ExchangeEntry> store =
            new ConcurrentHashMap<>();

    /**
     * Creates a new one-time code mapped to the given user ID.
     *
     * @param userId the application User's UUID
     * @return the generated opaque code (a random UUID string)
     */
    public String store(UUID userId) {
        evictExpired();

        String code = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(CODE_TTL_SECONDS, ChronoUnit.SECONDS);

        store.put(code, new OAuth2ExchangeEntry(userId, expiresAt));
        return code;
    }

    /**
     * Atomically validates and consumes the code.
     *
     * @param code the code received from Angular
     * @return the associated OAuth2ExchangeEntry, or null if the code is
     *         unknown, expired, or already consumed
     */
    public OAuth2ExchangeEntry consume(String code) {
        OAuth2ExchangeEntry entry = store.get(code);

        if (entry == null) {
            return null;   // Unknown code
        }

        if (entry.isExpired()) {
            store.remove(code);
            return null;   // Expired
        }

        boolean firstConsumer = entry.consume();
        if (!firstConsumer) {
            return null;   // Already used
        }

        // Remove from map — it has been consumed and will not be needed again.
        store.remove(code);
        return entry;
    }

    /** Removes entries that have already expired from the map. */
    private void evictExpired() {
        store.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}
