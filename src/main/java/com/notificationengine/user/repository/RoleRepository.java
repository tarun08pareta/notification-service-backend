package com.notificationengine.user.repository;

import com.notificationengine.user.domain.Role;
import com.notificationengine.user.domain.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);



    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            UUID id
    );

    Page<Role> findByNameContainingIgnoreCaseAndStatus(
            String name,
            RoleStatus status,
            Pageable pageable
    );

    Page<Role> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );
}
