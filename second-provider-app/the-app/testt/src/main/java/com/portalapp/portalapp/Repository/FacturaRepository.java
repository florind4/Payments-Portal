package com.portalapp.portalapp.Repository;

import com.portalapp.portalapp.Model.Factura;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Factura findTopByOrderByCreatedAtDesc();
}
