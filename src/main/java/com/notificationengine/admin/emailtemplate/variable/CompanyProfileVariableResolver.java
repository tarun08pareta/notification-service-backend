package com.notificationengine.admin.emailtemplate.variable;

import com.notificationengine.company.domain.CompanyProfile;
import com.notificationengine.company.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyProfileVariableResolver {

    private final CompanyProfileRepository companyProfileRepository;


    public Map<String, Object> resolve(UUID userId) {

        CompanyProfile profile =
                companyProfileRepository.findByUserId(userId)
                        .orElse(null);

        Map<String, Object> variables = new HashMap<>();

        // Resolve company name from the existing company profile.
        if (profile != null && profile.getCompanyName() != null) {
            variables.put(
                    "companyName",
                    profile.getCompanyName()
            );
        }

        // Resolve company logo URL from the existing company profile.
        if (profile != null && profile.getLogoUrl() != null) {
            variables.put(
                    "logoUrl",
                    profile.getLogoUrl()
            );
        }

        return variables;
    }
}
