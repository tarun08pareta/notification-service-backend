package com.notificationengine.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StorageUploadResult upload(String objectKey, MultipartFile file);

    void delete(String objectKey);

    String getUrl(String objectKey);
}