package com.mobileapp.controller;

import com.mobileapp.model.Provider;
import com.mobileapp.model.Connection;
import com.mobileapp.dto.ProviderAuthRequest;
import com.mobileapp.dto.ProviderAuthResponse;
import com.mobileapp.service.ProviderService;
import com.mobileapp.service.ConnectionService;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/providers")
public class ProviderAuthController {
    private static final Logger logger = LoggerFactory.getLogger(ProviderAuthController.class);

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/{providerId}/auth")
    public ResponseEntity<?> authenticateWithProvider(
            @PathVariable Long providerId,
            @RequestBody ProviderAuthRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        logger.info("Received authentication request for provider ID: {}", providerId);
        logger.debug("Request details - Username: {}", request.getUsername());

        try {
            String loggedInUsername = userService.getUsernameFromToken(authHeader);
            logger.info("Current logged-in portal user: {}", loggedInUsername);

            Provider provider = providerService.getProviderById(providerId);
            if (provider == null) {
                logger.error("Provider not found with ID: {}", providerId);
                return ResponseEntity.badRequest().body("Provider not found");
            }

            logger.info("Found provider: {}", provider.getName());
            logger.debug("Provider URL: {}", provider.getUrl());

            String providerUrl = provider.getUrl();
            if (!providerUrl.endsWith("/")) {
                providerUrl += "/";
            }
            providerUrl += "api/auth/portal-auth";
            logger.info("Sending request to provider URL: {}", providerUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authHeader);
            headers.set("X-Portal-Username", loggedInUsername);

            ProviderAuthRequest providerRequest = new ProviderAuthRequest();
            providerRequest.setUsername(request.getUsername());
            providerRequest.setPassword(request.getPassword());
            providerRequest.setSecret(provider.getSecret());

            try {
                String outgoingJson = new ObjectMapper().writeValueAsString(providerRequest);
                logger.info("Outgoing JSON to provider: {}", outgoingJson);
                logger.info("Portal username in header: {}", loggedInUsername);
                logger.info("Username in request: {}", providerRequest.getUsername());
                logger.info("Secret in request: {}", providerRequest.getSecret());
            } catch (Exception ex) {
                logger.error("Could not serialize outgoing JSON: {}", ex.getMessage());
            }

            logger.info("Sending request to provider with:");
            logger.info("- Provider Username: {}", providerRequest.getUsername());
            logger.info("- Portal Username: {}", loggedInUsername);
            logger.info("- Provider Name: {}", provider.getName());

            HttpEntity<ProviderAuthRequest> entity = new HttpEntity<>(providerRequest, headers);
            logger.debug("Request body prepared with username and secret");

            ResponseEntity<ProviderAuthResponse> response = restTemplate.postForEntity(
                providerUrl,
                entity,
                ProviderAuthResponse.class
            );

            logger.info("Received response from provider with status: {}", response.getStatusCode());
            logger.debug("Response body: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ProviderAuthResponse authResponse = response.getBody();
                if (authResponse.isSuccess()) {
                    logger.info("Successfully authenticated with provider");

                    System.out.println("\n==========================================");
                    System.out.println("THE KEY IS : " + authResponse.getKey());
                    System.out.println("==========================================\n");

                    Connection connection = new Connection();
                    connection.setUsername(loggedInUsername);
                    connection.setFurnizor(provider.getName());
                    connection.setKey(authResponse.getKey());

                    connectionService.saveConnection(connection);
                    logger.info("Connection stored in portal's conexiuni table for user: {} with provider: {} and key: {}", 
                        loggedInUsername, provider.getName(), authResponse.getKey());
                    
                    return ResponseEntity.ok(authResponse);
                } else {
                    logger.error("Provider authentication failed: {}", authResponse.getMessage());
                    return ResponseEntity.badRequest().body(authResponse);
                }
            } else {
                logger.error("Provider returned non-success status: {}", response.getStatusCode());
                return ResponseEntity.badRequest().body("Failed to authenticate with provider");
            }

        } catch (Exception e) {
            logger.error("Error during provider authentication", e);
            return ResponseEntity.internalServerError().body("An error occurred during authentication");
        }
    }
} 