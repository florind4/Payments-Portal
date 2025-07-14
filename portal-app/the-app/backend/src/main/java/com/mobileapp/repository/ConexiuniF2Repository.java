package com.mobileapp.repository;

import com.mobileapp.model.ConexiuniF2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConexiuniF2Repository extends JpaRepository<ConexiuniF2, Long> {
    Optional<ConexiuniF2> findByPortalname(String portalname);
} 