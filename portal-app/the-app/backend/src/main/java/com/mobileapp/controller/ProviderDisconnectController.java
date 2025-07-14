package com.mobileapp.controller;

import com.mobileapp.service.ConexiuniF1Service;
import com.mobileapp.service.ConexiuniF2Service;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/provider-disconnect")
@CrossOrigin(origins = "*")
public class ProviderDisconnectController {
    private static final Logger logger = LoggerFactory.getLogger(ProviderDisconnectController.class);

    @Autowired
    private ConexiuniF1Service conexiuniF1Service;

    @Autowired
    private ConexiuniF2Service conexiuniF2Service;

    @Autowired
    private UserService userService;

    @GetMapping("/status")
    public ResponseEntity<?> getConnectionStatus(@RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to get connection status");
            String username = userService.getUsernameFromToken(token);
            logger.info("Current user: {}", username);

            Map<String, Boolean> status = new HashMap<>();

            boolean hasF1Connection = conexiuniF1Service.findByPortalname(username).isPresent();
            status.put("hasF1Connection", hasF1Connection);
            logger.info("F1 connection status for user {}: {}", username, hasF1Connection);

            boolean hasF2Connection = conexiuniF2Service.findByPortalname(username).isPresent();
            status.put("hasF2Connection", hasF2Connection);
            logger.info("F2 connection status for user {}: {}", username, hasF2Connection);

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Error getting connection status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error getting connection status: " + e.getMessage());
            }});
        }
    }

    @DeleteMapping("/f1")
    public ResponseEntity<?> disconnectFromF1(@RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to disconnect from F1");
            String username = userService.getUsernameFromToken(token);
            logger.info("Disconnecting user: {} from F1", username);

            conexiuniF1Service.deleteByPortalname(username);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully disconnected from F-Telecom");
            logger.info("Successfully disconnected user {} from F1", username);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error disconnecting from F1: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error disconnecting from F-Telecom: " + e.getMessage());
            }});
        }
    }

    @DeleteMapping("/f2")
    public ResponseEntity<?> disconnectFromF2(@RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to disconnect from F2");
            String username = userService.getUsernameFromToken(token);
            logger.info("Disconnecting user: {} from F2", username);

            conexiuniF2Service.deleteByPortalname(username);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully disconnected from F-Electrica");
            logger.info("Successfully disconnected user {} from F2", username);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error disconnecting from F2: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error disconnecting from F-Electrica: " + e.getMessage());
            }});
        }
    }
} 