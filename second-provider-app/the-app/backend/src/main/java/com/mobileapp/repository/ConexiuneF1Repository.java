package com.mobileapp.repository;

import com.mobileapp.model.ConexiuneF1;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConexiuneF1Repository extends JpaRepository<ConexiuneF1, Long> {
    Optional<ConexiuneF1> findByUsernameAndDev(String username, String dev);
    boolean existsByUsernameAndDev(String username, String dev);
} 