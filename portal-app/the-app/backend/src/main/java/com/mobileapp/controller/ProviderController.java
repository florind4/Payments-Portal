package com.mobileapp.controller;

import com.mobileapp.model.Provider;
import com.mobileapp.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*")
public class ProviderController {
    private static final Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @Autowired
    private ProviderRepository providerRepository;

    @GetMapping
    public ResponseEntity<?> getProviders() {
        try {
            logger.info("Received request to get all providers");
            List<Provider> providers = providerRepository.findAll();
            logger.info("Successfully retrieved {} providers", providers.size());
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            logger.error("Error retrieving providers: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving providers: " + e.getMessage());
        }
    }
} 