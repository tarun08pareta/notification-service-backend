package com.notificationengine.company.controller;

import com.notificationengine.company.dto.request.UpdateCompanyProfileRequest;
import com.notificationengine.company.dto.response.CompanyProfileResponse;
import com.notificationengine.company.service.CompanyProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/company/profile")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    @GetMapping
    public ResponseEntity<CompanyProfileResponse> getProfile(
            Authentication authentication
    ) {

        CompanyProfileResponse response =
                companyProfileService.getProfile(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CompanyProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateCompanyProfileRequest request
    ) {

        CompanyProfileResponse response =
                companyProfileService.updateProfile(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/logo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CompanyProfileResponse> uploadLogo(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {

        // UPDATED: Receives the real image file instead of a logo URL.
        return ResponseEntity.ok(
                companyProfileService.uploadLogo(
                        authentication.getName(),
                        file
                )
        );
    }

    @DeleteMapping("/logo")
    public ResponseEntity<CompanyProfileResponse> deleteLogo(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                companyProfileService.deleteLogo(
                        authentication.getName()
                )
        );
    }
}