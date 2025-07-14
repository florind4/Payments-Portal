package com.mobileapp.service;

import com.mobileapp.model.ConexiuniF1;
import com.mobileapp.repository.ConexiuniF1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConexiuniF1Service {
    private static final Logger logger = LoggerFactory.getLogger(ConexiuniF1Service.class);

    @Autowired
    private ConexiuniF1Repository conexiuniF1Repository;

    public String saveConexiune(String username, String providerName, String portalUsername) {
        logger.info("Saving new conexiune with details:");
        logger.info("- Provider Username: {}", username);
        logger.info("- Provider Name: {}", providerName);
        logger.info("- Portal Username: {}", portalUsername);
        
        ConexiuniF1 conexiune = new ConexiuniF1();
        conexiune.setUsername(username);
        conexiune.setKey(generateKey());
        conexiune.setPortalname(portalUsername);
        
        logger.info("Created ConexiuniF1 object with:");
        logger.info("- Username: {}", conexiune.getUsername());
        logger.info("- Key: {}", conexiune.getKey());
        logger.info("- Portalname: {}", conexiune.getPortalname());
        
        ConexiuniF1 savedConexiune = conexiuniF1Repository.save(conexiune);
        logger.info("Successfully saved conexiune to database with ID: {}", savedConexiune.getId());
        
        return savedConexiune.getKey();
    }

    public Optional<ConexiuniF1> findByPortalname(String portalname) {
        logger.info("Looking for ConexiuniF1 with portalname: {}", portalname);
        Optional<ConexiuniF1> conexiune = conexiuniF1Repository.findByPortalname(portalname);
        if (conexiune.isPresent()) {
            logger.info("Found ConexiuniF1 connection for portalname: {}", portalname);
        } else {
            logger.info("No ConexiuniF1 connection found for portalname: {}", portalname);
        }
        return conexiune;
    }

    public void deleteByPortalname(String portalname) {
        logger.info("Deleting ConexiuniF1 connection for portalname: {}", portalname);
        Optional<ConexiuniF1> conexiune = conexiuniF1Repository.findByPortalname(portalname);
        if (conexiune.isPresent()) {
            conexiuniF1Repository.delete(conexiune.get());
            logger.info("Successfully deleted ConexiuniF1 connection for portalname: {}", portalname);
        } else {
            logger.warn("No ConexiuniF1 connection found to delete for portalname: {}", portalname);
        }
    }

    private String generateKey() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
} 