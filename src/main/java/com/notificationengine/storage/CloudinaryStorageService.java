package com.notificationengine.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CloudinaryStorageService implements StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    private final Cloudinary cloudinary;

    @Override
    public StorageUploadResult upload(
            String objectKey,
            MultipartFile file
    ) {

        validateImage(file);

        try {
            // UPDATED: Cloudinary stores the uploaded image using the
            // deterministic object key supplied by the business layer.
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", objectKey,
                            "resource_type", "image",
                            "overwrite", true,
                            "invalidate", true
                    )
            );

            Object secureUrl = result.get("secure_url");

            if (secureUrl == null) {
                throw new IllegalStateException(
                        "Cloudinary did not return a secure URL"
                );
            }

            return new StorageUploadResult(
                    secureUrl.toString(),
                    objectKey
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to upload image to Cloudinary",
                    exception
            );
        }
    }

    @Override
    public void delete(String objectKey) {

        try {
            cloudinary.uploader().destroy(
                    objectKey,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to delete image from Cloudinary",
                    exception
            );
        }
    }

    @Override
    public String getUrl(String objectKey) {

        return cloudinary
                .url()
                .secure(true)
                .generate(objectKey);
    }

    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Logo file must not be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Logo file must not exceed 2 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {

            throw new IllegalArgumentException(
                    "Only PNG, JPEG, and WEBP images are allowed"
            );
        }
    }
}