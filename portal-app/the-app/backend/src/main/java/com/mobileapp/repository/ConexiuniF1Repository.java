package com.mobileapp.repository;

import com.mobileapp.model.ConexiuniF1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConexiuniF1Repository extends JpaRepository<ConexiuniF1, Long> {
    Optional<ConexiuniF1> findByPortalname(String portalname);
} 