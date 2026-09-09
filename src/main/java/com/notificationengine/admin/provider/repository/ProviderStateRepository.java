package com.notificationengine.admin.provider.repository;

import com.notificationengine.admin.provider.domain.ProviderState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProviderStateRepository extends JpaRepository<ProviderState, UUID> {
    Optional<ProviderState> findByProvider(String provider);
}
