package com.mobileapp.repository;

import com.mobileapp.entity.UtilizatorF1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilizatorF1Repository extends JpaRepository<UtilizatorF1, Long> {
    Optional<UtilizatorF1> findByUsername(String username);
    Optional<UtilizatorF1> findByEmail(String email);
    Optional<UtilizatorF1> findByCode(Integer code);
} 