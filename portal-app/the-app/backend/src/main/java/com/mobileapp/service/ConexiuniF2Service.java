package com.mobileapp.service;

import com.mobileapp.model.ConexiuniF2;
import com.mobileapp.repository.ConexiuniF2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConexiuniF2Service {
    private static final Logger logger = LoggerFactory.getLogger(ConexiuniF2Service.class);

    @Autowired
    private ConexiuniF2Repository conexiuniF2Repository;

    public String saveConexiune(String username, String providerName, String portalUsername) {
        logger.info("Saving new conexiune with details:");
        logger.info("- Provider Username: {}", username);
        logger.info("- Provider Name: {}", providerName);
        logger.info("- Portal Username: {}", portalUsername);
        
        ConexiuniF2 conexiune = new ConexiuniF2();
        conexiune.setUsername(username);
        conexiune.setKey(generateKey());
        conexiune.setPortalname(portalUsername);
        
        logger.info("Created ConexiuniF2 object with:");
        logger.info("- Username: {}", conexiune.getUsername());
        logger.info("- Key: {}", conexiune.getKey());
        logger.info("- Portalname: {}", conexiune.getPortalname());
        
        ConexiuniF2 savedConexiune = conexiuniF2Repository.save(conexiune);
        logger.info("Successfully saved conexiune to database with ID: {}", savedConexiune.getId());
        
        return savedConexiune.getKey();
    }

    public Optional<ConexiuniF2> findByPortalname(String portalname) {
        logger.info("Looking for ConexiuniF2 with portalname: {}", portalname);
        Optional<ConexiuniF2> conexiune = conexiuniF2Repository.findByPortalname(portalname);
        if (conexiune.isPresent()) {
            logger.info("Found ConexiuniF2 connection for portalname: {}", portalname);
        } else {
            logger.info("No ConexiuniF2 connection found for portalname: {}", portalname);
        }
        return conexiune;
    }

    public void deleteByPortalname(String portalname) {
        logger.info("Deleting ConexiuniF2 connection for portalname: {}", portalname);
        Optional<ConexiuniF2> conexiune = conexiuniF2Repository.findByPortalname(portalname);
        if (conexiune.isPresent()) {
            conexiuniF2Repository.delete(conexiune.get());
            logger.info("Successfully deleted ConexiuniF2 connection for portalname: {}", portalname);
        } else {
            logger.warn("No ConexiuniF2 connection found to delete for portalname: {}", portalname);
        }
    }

    private String generateKey() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
} 