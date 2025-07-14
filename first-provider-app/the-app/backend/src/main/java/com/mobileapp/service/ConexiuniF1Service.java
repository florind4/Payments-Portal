package com.mobileapp.service;

import com.mobileapp.model.ConexiuneF1;
import com.mobileapp.repository.ConexiuneF1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConexiuniF1Service {
    private static final Logger logger = LoggerFactory.getLogger(ConexiuniF1Service.class);

    @Autowired
    private ConexiuneF1Repository conexiuneF1Repository;

    public String saveConexiune(String username, String providerName, String portalUsername) {
        try {
            logger.info("Starting saveConexiune with parameters:");
            logger.info("- username: {}", username);
            logger.info("- providerName: {}", providerName);
            logger.info("- portalUsername: {}", portalUsername);
            
            ConexiuneF1 conexiune = new ConexiuneF1();
            conexiune.setUsername(username);
            conexiune.setDev(providerName);
            conexiune.setKey(generateKey());
            conexiune.setPortalname(portalUsername);
            
            logger.info("Created ConexiuneF1 object with:");
            logger.info("- Username: {}", conexiune.getUsername());
            logger.info("- Dev: {}", conexiune.getDev());
            logger.info("- Key: {}", conexiune.getKey());
            logger.info("- Portalname: {}", conexiune.getPortalname());
            
            logger.info("Attempting to save to database...");
            ConexiuneF1 savedConexiune = conexiuneF1Repository.save(conexiune);
            logger.info("Successfully saved conexiune to database with ID: {}", savedConexiune.getId());
            
            return savedConexiune.getKey();
        } catch (Exception e) {
            logger.error("Error in saveConexiune: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String generateKey() {
        String key = UUID.randomUUID().toString().replace("-", "");
        logger.info("Generated new key: {}", key);
        return key;
    }

    public List<ConexiuneF1> getAllConnections() {
        return conexiuneF1Repository.findAll();
    }

    public ConexiuneF1 getConnectionByUsernameAndDev(String username, String dev) {
        return conexiuneF1Repository.findByUsernameAndDev(username, dev)
                .orElseThrow(() -> new RuntimeException("Connection not found"));
    }

    public boolean existsByUsernameAndDev(String username, String dev) {
        return conexiuneF1Repository.existsByUsernameAndDev(username, dev);
    }
} 