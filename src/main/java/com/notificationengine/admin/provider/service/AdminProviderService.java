package com.notificationengine.admin.provider.service;

import com.notificationengine.admin.provider.domain.ProviderState;
import com.notificationengine.admin.provider.repository.ProviderStateRepository;
import com.notificationengine.notification.provider.ProviderStateResolver;
import com.notificationengine.notification.provider.config.NotificationProviderProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminProviderService
        implements ProviderStateResolver {

    private final ProviderStateRepository providerStateRepository;
    private final NotificationProviderProperties properties;

    public AdminProviderService(
            ProviderStateRepository providerStateRepository,
            NotificationProviderProperties properties) {

        this.providerStateRepository = providerStateRepository;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeProviderStates() {

        properties.getProviders().forEach((providerName, config) -> {

            if (providerStateRepository
                    .findByProvider(providerName)
                    .isEmpty()) {

                providerStateRepository.save(
                        new ProviderState(
                                providerName,
                                config.isEnabled(),
                                config.getPriority()
                        )
                );
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEnabled(String providerName) {

        return providerStateRepository
                .findByProvider(providerName)
                .map(ProviderState::isEnabled)
                .orElseGet(() -> getConfiguredEnabledState(providerName));
    }

    @Override
    @Transactional(readOnly = true)
    public int getPriority(String providerName) {

        return providerStateRepository
                .findByProvider(providerName)
                .map(ProviderState::getPriority)
                .orElseGet(() -> getConfiguredPriority(providerName));
    }

    @Transactional
    public ProviderState updateEnabledState(
            String providerName,
            boolean enabled
    ) {

        validateConfiguredProvider(providerName);

        ProviderState state = getOrCreateState(providerName);

        state.setEnabled(enabled);

        return providerStateRepository.save(state);
    }

    @Transactional
    public ProviderState updatePriority(
            String providerName,
            int priority
    ) {

        validateConfiguredProvider(providerName);

        if (priority < 0) {
            throw new IllegalArgumentException(
                    "Provider priority cannot be negative"
            );
        }

        ProviderState state = getOrCreateState(providerName);

        state.setPriority(priority);

        return providerStateRepository.save(state);
    }

    @Transactional(readOnly = true)
    public List<ProviderState> getAllProviderStates() {

        return properties.getProviders()
                .keySet()
                .stream()
                .map(providerName ->
                        providerStateRepository
                                .findByProvider(providerName)
                                .orElseGet(() -> {

                                    NotificationProviderProperties.ProviderConfig config =
                                            properties.getProviders().get(providerName);

                                    return new ProviderState(
                                            providerName,
                                            config.isEnabled(),
                                            config.getPriority()
                                    );
                                })
                )
                .toList();
    }

    private ProviderState getOrCreateState(
            String providerName
    ) {

        return providerStateRepository
                .findByProvider(providerName)
                .orElseGet(() -> {

                    NotificationProviderProperties.ProviderConfig config =
                            properties.getProviders().get(providerName);

                    return new ProviderState(
                            providerName,
                            config.isEnabled(),
                            config.getPriority()
                    );
                });
    }

    private boolean getConfiguredEnabledState(
            String providerName
    ) {

        NotificationProviderProperties.ProviderConfig config =
                properties.getProviders().get(providerName);

        if (config == null) {
            throw new IllegalStateException(
                    "No configuration found for provider: "
                            + providerName
            );
        }

        return config.isEnabled();
    }

    private int getConfiguredPriority(
            String providerName
    ) {

        NotificationProviderProperties.ProviderConfig config =
                properties.getProviders().get(providerName);

        if (config == null) {
            throw new IllegalStateException(
                    "No configuration found for provider: "
                            + providerName
            );
        }

        return config.getPriority();
    }

    private void validateConfiguredProvider(
            String providerName
    ) {

        if (!properties.getProviders().containsKey(providerName)) {
            throw new IllegalArgumentException(
                    "Unknown provider: " + providerName
            );
        }
    }
}