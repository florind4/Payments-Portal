package com.mobileapp.controller;

import com.mobileapp.model.Provider;
import com.mobileapp.service.ProviderService;
import com.mobileapp.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/providers")
public class ProviderAuthController {
    private static final Logger logger = LoggerFactory.getLogger(ProviderAuthController.class);

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/{providerId}/auth")
    public ResponseEntity<?> authenticateWithProvider(
            @PathVariable Long providerId,
            @RequestBody Map<String, String> credentials) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication required",
                    "message", "User must be logged in"
                ));
            }
            String username = auth.getName();
            logger.info("Processing authentication for user: {}", username);

            // Get provider details
            Provider provider = providerService.getProviderById(providerId);
            if (provider == null) {
                logger.error("Provider not found with id: {}", providerId);
                return ResponseEntity.notFound().build();
            }
            logger.info("Found provider: {}", provider.getName());

            // Prepare the request to the provider's authentication endpoint
            String providerUrl = provider.getUrl() + "/auth";
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", credentials.get("username"));
            requestBody.put("password", credentials.get("password"));
            requestBody.put("secret", provider.getSecret());

            logger.info("Sending request to provider URL: {}", providerUrl);

            // Send the request to the provider
            ResponseEntity<Map> response = restTemplate.postForEntity(
                providerUrl,
                requestBody,
                Map.class
            );

            logger.info("Received response from provider: {}", response.getBody());

            // Extract the key from the response
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("key")) {
                String key = (String) responseBody.get("key");
                logger.info("Received key from provider for user {} and provider {}", username, provider.getName());
                
                try {
                    // Save the connection
                    connectionService.saveConnection(username, provider.getName(), key);
                    logger.info("Successfully saved connection for user {} and provider {}", username, provider.getName());
                    
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Successfully connected to provider",
                        "provider", provider.getName()
                    ));
                } catch (Exception e) {
                    logger.error("Failed to save connection: {}", e.getMessage(), e);
                    return ResponseEntity.status(500).body(Map.of(
                        "error", "Failed to save connection",
                        "message", e.getMessage()
                    ));
                }
            } else {
                logger.error("Invalid response from provider. Response body: {}", responseBody);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid response from provider",
                    "message", "Provider response did not contain a key",
                    "response", responseBody
                ));
            }
        } catch (Exception e) {
            logger.error("Error during provider authentication: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to authenticate with provider",
                "message", e.getMessage()
            ));
        }
    }
} 