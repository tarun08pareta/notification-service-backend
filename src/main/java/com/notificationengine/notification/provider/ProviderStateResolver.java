package com.notificationengine.notification.provider;

public interface ProviderStateResolver {
    boolean isEnabled(String providerName);
    int getPriority(String providerName);
}
