package com.legira.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AbstractUserRepository extends JpaRepository<AbstractUser, UUID> {
    Optional<AbstractUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
