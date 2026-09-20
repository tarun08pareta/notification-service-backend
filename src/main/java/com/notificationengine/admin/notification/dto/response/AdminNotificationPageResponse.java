package com.notificationengine.admin.notification.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class AdminNotificationPageResponse {
    private final List<AdminNotificationResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;

    public AdminNotificationPageResponse(
            List<AdminNotificationResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }
}
