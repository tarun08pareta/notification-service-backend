package com.notificationengine.user.repository;

import com.notificationengine.user.domain.User;
import com.notificationengine.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface  UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    // for admin pagination search
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            UUID id
    );

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN u.roles r
            WHERE
                (
                    :search IS NULL
                    OR :search = ''
                    OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                AND (
                    :status IS NULL
                    OR u.status = :status
                )
                AND (
                    :role IS NULL
                    OR :role = ''
                    OR LOWER(r.name) = LOWER(:role)
                )
            """)
    Page<User> findAdminUsers(
            @Param("search") String search,
            @Param("status") UserStatus status,
            @Param("role") String role,
            Pageable pageable
    );
}
