package com.mobileapp.controller;

import com.mobileapp.dto.DeveloperDeleteRequest;
import com.mobileapp.dto.DeveloperLoginRequest;
import com.mobileapp.dto.DeveloperLoginResponse;
import com.mobileapp.dto.DeveloperProfileUpdateRequest;
import com.mobileapp.dto.DeveloperRegisterRequest;
import com.mobileapp.entity.Developer;
import com.mobileapp.service.DeveloperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/developers")
@CrossOrigin(origins = "*")
public class DeveloperController {
    private static final Logger logger = LoggerFactory.getLogger(DeveloperController.class);

    @Autowired
    private DeveloperService developerService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody DeveloperRegisterRequest request) {
        try {
            logger.info("Received registration request for email: {}", request.getEmail());
            Developer developer = developerService.register(request);
            logger.info("Successfully registered developer with email: {}", request.getEmail());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(developer);
        } catch (RuntimeException e) {
            logger.error("Registration failed for email: {} - Error: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody DeveloperLoginRequest request) {
        try {
            logger.info("Received login request for email/name: {}", request.getEmailOrName());
            DeveloperLoginResponse response = developerService.login(request);
            logger.info("Successfully logged in developer with email/name: {}", request.getEmailOrName());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            logger.error("Login failed for email/name: {} - Error: {}", request.getEmailOrName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token,
                                      @RequestHeader("X-Developer-Secret") String secret) {
        try {
            logger.info("Received profile request");
            Developer developer = developerService.getProfile(token, secret);
            if (developer == null) {
                logger.error("Developer not found");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\": \"Developer not found\"}");
            }
            logger.info("Successfully retrieved profile for developer: {}", developer.getEmail());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(developer);
        } catch (RuntimeException e) {
            logger.error("Failed to get profile - Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
                                         @RequestHeader("X-Developer-Secret") String secret,
                                         @RequestBody DeveloperProfileUpdateRequest request) {
        try {
            logger.info("Received profile update request");
            Developer developer = developerService.updateProfile(token, secret, request);
            logger.info("Successfully updated profile for developer: {}", developer.getEmail());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(developer);
        } catch (RuntimeException e) {
            logger.error("Failed to update profile - Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String token,
                                         @RequestHeader("X-Developer-Secret") String secret,
                                         @RequestBody DeveloperDeleteRequest request) {
        try {
            logger.info("Received account deletion request");
            developerService.deleteAccount(token, secret, request.getPassword());
            logger.info("Successfully deleted developer account");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"Account deleted successfully\"}");
        } catch (RuntimeException e) {
            logger.error("Failed to delete account - Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
} 