package com.notificationengine.admin.provider.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notification_provider_states",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_provider_states_provider",
                        columnNames = "provider"
                )
        }
)
@Getter
@Setter
public class ProviderState {
    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private int priority;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    protected ProviderState() {
    }

    public ProviderState(
            String provider,
            boolean enabled,
            int priority
    ) {
        this.id = UUID.randomUUID();
        this.provider = provider;
        this.enabled = enabled;
        this.priority =priority;
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
