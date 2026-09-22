package com.notificationengine.company.repository;

import com.notificationengine.company.domain.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, UUID> {

    Optional<CompanyProfile> findByUserId(UUID userId);
}