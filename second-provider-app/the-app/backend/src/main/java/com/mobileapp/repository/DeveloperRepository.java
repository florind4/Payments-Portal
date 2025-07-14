package com.mobileapp.repository;

import com.mobileapp.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Optional<Developer> findByEmail(String email);
    Optional<Developer> findByName(String name);
    Optional<Developer> findBySecret(String secret);
    boolean existsByEmail(String email);
} 