package com.mobileapp.controller;

import com.mobileapp.dto.ProviderAuthRequest;
import com.mobileapp.service.ConexiuniF1Service;
import com.mobileapp.service.ConexiuniF2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UnifiedAuthController {
    private static final Logger logger = LoggerFactory.getLogger(UnifiedAuthController.class);

    @Autowired
    private ConexiuniF1Service conexiuniF1Service;

    @Autowired
    private ConexiuniF2Service conexiuniF2Service;

    @PostMapping("/portal-auth")
    public ResponseEntity<?> authenticatePortalUser(@RequestBody ProviderAuthRequest request) {
        logger.info("Received portal authentication request");
        logger.info("Request details:");
        logger.info("- Provider Username: {}", request.getUsername());
        logger.info("- Portal Username: {}", request.getPortalUsername());
        logger.info("- Secret: {}", request.getSecret());
        logger.debug("Full request body: {}", request);

        try {
            String key;
            String providerName;

            if ("f1_secret_key_2024".equals(request.getSecret())) {
                providerName = "F-Telecom";
                logger.info("Processing F-Telecom authentication");
                key = conexiuniF1Service.saveConexiune(
                    request.getUsername(), 
                    providerName, 
                    request.getPortalUsername()
                );
            } else if ("f2_secret_key_2024".equals(request.getSecret())) {
                providerName = "F-Electrica";
                logger.info("Processing F-Electrica authentication");
                key = conexiuniF2Service.saveConexiune(
                    request.getUsername(), 
                    providerName, 
                    request.getPortalUsername()
                );
            } else {
                logger.error("Invalid secret key provided: {}", request.getSecret());
                return ResponseEntity.badRequest().body("Invalid secret key");
            }

            logger.info("Successfully created {} connection with key: {}", providerName, key);

            return ResponseEntity.ok().body("Connection established successfully with key: " + key);
        } catch (Exception e) {
            logger.error("Error creating connection: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Failed to establish connection: " + e.getMessage());
        }
    }
} 