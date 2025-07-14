package com.mobileapp.service;

import com.mobileapp.entity.FacturaF1;
import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.FacturaF1Repository;
import com.mobileapp.repository.UtilizatorF1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacturaF1Service {
    private static final Logger logger = LoggerFactory.getLogger(FacturaF1Service.class);
    
    @Autowired
    private FacturaF1Repository facturaF1Repository;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Transactional
    public FacturaF1 createFactura(FacturaF1 factura) {
        try {
            logger.info("Attempting to save factura: {}", factura);
            FacturaF1 savedFactura = facturaF1Repository.save(factura);
            logger.info("Successfully saved factura with id: {}", savedFactura.getId());
            return savedFactura;
        } catch (Exception e) {
            logger.error("Error saving factura: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save factura: " + e.getMessage(), e);
        }
    }

    public UtilizatorF1 getUserByUsername(String username) {
        try {
            logger.info("Attempting to find user with username: {}", username);
            return utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with username: {}", username);
                        return new RuntimeException("User not found with username: " + username);
                    });
        } catch (Exception e) {
            logger.error("Error finding user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find user: " + e.getMessage(), e);
        }
    }

    public List<FacturaF1> getBillsByUsername(String username) {
        try {
            logger.info("Attempting to retrieve bills for username: {}", username);
            List<FacturaF1> bills = facturaF1Repository.findByUsername(username);
            logger.info("Successfully retrieved {} bills for username: {}", bills.size(), username);
            return bills;
        } catch (Exception e) {
            logger.error("Error retrieving bills for username {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve bills: " + e.getMessage(), e);
        }
    }

    public List<FacturaF1> getAllFacturi() {
        try {
            logger.info("Attempting to retrieve all facturi");
            List<FacturaF1> facturi = facturaF1Repository.findAll();
            logger.info("Successfully retrieved {} facturi", facturi.size());
            return facturi;
        } catch (Exception e) {
            logger.error("Error retrieving all facturi: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve facturi: " + e.getMessage(), e);
        }
    }

    public FacturaF1 getFacturaById(Long id) {
        try {
            logger.info("Attempting to retrieve factura with id: {}", id);
            return facturaF1Repository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Factura not found with id: {}", id);
                        return new RuntimeException("Factura not found with id: " + id);
                    });
        } catch (Exception e) {
            logger.error("Error retrieving factura with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve factura: " + e.getMessage(), e);
        }
    }
} 