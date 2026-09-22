package com.notificationengine.company.service;



import com.notificationengine.company.domain.CompanyProfile;
import com.notificationengine.company.dto.request.UpdateCompanyProfileRequest;
import com.notificationengine.company.dto.response.CompanyProfileResponse;
import com.notificationengine.company.repository.CompanyProfileRepository;
import com.notificationengine.storage.StorageService;
import com.notificationengine.storage.StorageUploadResult;
import com.notificationengine.user.domain.User;
import com.notificationengine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public CompanyProfileResponse getProfile(String email) {
        User user = getUserByEmail(email);

        return companyProfileRepository.findByUserId(user.getId())
                .map(this::toResponse) // Agar profile hai, toh response banaye
                .orElseGet(CompanyProfileResponse::new); // Agar nahi hai, toh empty object bhej de
    }

    @Transactional
    public CompanyProfileResponse updateProfile(
            String email,
            UpdateCompanyProfileRequest request
    ) {

        User user = getUserByEmail(email);

        CompanyProfile profile =
                companyProfileRepository.findByUserId(user.getId())
                        .orElseGet(() -> {
                            CompanyProfile newProfile =
                                    new CompanyProfile();

                            newProfile.setUserId(user.getId());

                            return newProfile;
                        });

        profile.setCompanyName(request.getCompanyName());
        profile.setSenderName(request.getSenderName());
        profile.setSenderEmail(request.getSenderEmail());
        profile.setReplyToEmail(request.getReplyToEmail());
        profile.setSmsSenderId(request.getSmsSenderId());
        profile.setSmsSenderNumber(request.getSmsSenderNumber());

        CompanyProfile savedProfile =
                companyProfileRepository.save(profile);

        return toResponse(savedProfile);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );
    }

    private CompanyProfileResponse toResponse(
            CompanyProfile profile
    ) {

        return new CompanyProfileResponse(
                profile.getId(),
                profile.getCompanyName(),
                profile.getLogoUrl(),
                profile.getSenderName(),
                profile.getSenderEmail(),
                profile.getReplyToEmail(),
                profile.getSmsSenderId(),
                profile.getSmsSenderNumber(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );

    }
        // for logo
        @Transactional
        public CompanyProfileResponse uploadLogo(
                String email,
                MultipartFile file
                 ) {

            User user = getUserByEmail(email);

            CompanyProfile profile =
                    companyProfileRepository.findByUserId(user.getId())
                            .orElseGet(() -> {
                                CompanyProfile newProfile =
                                        new CompanyProfile();

                                newProfile.setUserId(user.getId());
                                newProfile.setCompanyName("");

                                return newProfile;
                            });

            String objectKey =
                    "company_profiles/"
                            + user.getId()
                            + "/logo";

            // UPDATED: Upload the actual image file to Cloudinary.
            StorageUploadResult uploadResult =
                    storageService.upload(objectKey, file);

            profile.setLogoUrl(uploadResult.url());
            profile.setLogoPublicId(uploadResult.objectKey());

            CompanyProfile savedProfile =
                    companyProfileRepository.save(profile);

            return toResponse(savedProfile);
        }

        @Transactional
        public CompanyProfileResponse deleteLogo(String email) {

            User user = getUserByEmail(email);

            CompanyProfile profile =
                    companyProfileRepository.findByUserId(user.getId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Company profile not found"
                                    )
                            );

            if (profile.getLogoPublicId() == null
                    || profile.getLogoPublicId().isBlank()) {

                return toResponse(profile);
            }

            // UPDATED: Delete the actual image from Cloudinary first.
            storageService.delete(profile.getLogoPublicId());

            profile.setLogoUrl(null);
            profile.setLogoPublicId(null);

            CompanyProfile savedProfile =
                    companyProfileRepository.save(profile);

            return toResponse(savedProfile);
        }


}