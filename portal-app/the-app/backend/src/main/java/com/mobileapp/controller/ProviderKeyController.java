package com.mobileapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider-keys")
@CrossOrigin(origins = "*")
public class ProviderKeyController {
    private static final Logger logger = LoggerFactory.getLogger(ProviderKeyController.class);

    @PostMapping("/receive")
    public ResponseEntity<?> receiveProviderKey(
            @RequestParam String providerName,
            @RequestParam String key) {

        System.out.flush();

        System.out.println("\n==========================================");
        System.out.println("RECEIVED NEW KEY FROM PROVIDER:");
        System.out.println("Provider: " + providerName);
        System.out.println("Key: " + key);
        System.out.println("==========================================\n");

        System.out.flush();

        logger.info("Received new key from provider: {} - Key: {}", providerName, key);
        
        return ResponseEntity.ok().body("Key received successfully");
    }
} 