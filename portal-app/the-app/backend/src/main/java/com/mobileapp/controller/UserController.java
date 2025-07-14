package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public ResponseEntity<?> getUserBalance(@RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to get user balance");
            String username = userService.getUsernameFromToken(token);
            logger.info("Extracted username from token: {}", username);
            
            Double balance = userService.getUserBalance(username);
            logger.info("Retrieved balance for user {}: {}", username, balance);
            
            Map<String, Double> response = new HashMap<>();
            response.put("balance", balance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting user balance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error getting user balance: " + e.getMessage());
            }});
        }
    }

    @GetMapping("/admin-status")
    public ResponseEntity<?> getAdminStatus(@RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to check admin status");
            String username = userService.getUsernameFromToken(token);
            logger.info("Extracted username from token: {}", username);
            
            boolean isAdmin = userService.isUserAdmin(username);
            logger.info("User {} admin status: {}", username, isAdmin);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("isAdmin", isAdmin);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking admin status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error checking admin status: " + e.getMessage());
            }});
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to get all users");
            String username = userService.getUsernameFromToken(token);
            logger.info("Extracted username from token: {}", username);
            
            if (!userService.isUserAdmin(username)) {
                logger.warn("Non-admin user {} attempted to access all users", username);
                return ResponseEntity.status(403).body(new HashMap<String, String>() {{
                    put("error", "Only admins can access this endpoint");
                }});
            }
            
            List<UtilizatorF1> users = userService.getAllUsers();
            logger.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error getting users: " + e.getMessage());
            }});
        }
    }

    @PutMapping("/{username}/admin")
    public ResponseEntity<?> updateUserAdminStatus(
            @PathVariable String username,
            @RequestBody Map<String, Boolean> request,
            @RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to update admin status for user {}", username);
            String adminUsername = userService.getUsernameFromToken(token);
            logger.info("Extracted admin username from token: {}", adminUsername);
            
            if (!userService.isUserAdmin(adminUsername)) {
                logger.warn("Non-admin user {} attempted to update admin status", adminUsername);
                return ResponseEntity.status(403).body(new HashMap<String, String>() {{
                    put("error", "Only admins can perform this action");
                }});
            }
            
            userService.updateUserAdminStatus(username, request.get("isAdmin"));
            logger.info("Successfully updated admin status for user {}", username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating user admin status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error updating admin status: " + e.getMessage());
            }});
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable String username,
            @RequestBody Map<String, String> updates,
            @RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to update details for user {}", username);
            String adminUsername = userService.getUsernameFromToken(token);
            logger.info("Extracted admin username from token: {}", adminUsername);
            
            if (!userService.isUserAdmin(adminUsername)) {
                logger.warn("Non-admin user {} attempted to update user details", adminUsername);
                return ResponseEntity.status(403).body(new HashMap<String, String>() {{
                    put("error", "Only admins can perform this action");
                }});
            }
            
            userService.updateUserDetails(username, updates);
            logger.info("Successfully updated details for user {}", username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating user details: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error updating user details: " + e.getMessage());
            }});
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to delete user {}", username);
            String adminUsername = userService.getUsernameFromToken(token);
            logger.info("Extracted admin username from token: {}", adminUsername);
            
            if (!userService.isUserAdmin(adminUsername)) {
                logger.warn("Non-admin user {} attempted to delete user", adminUsername);
                return ResponseEntity.status(403).body(new HashMap<String, String>() {{
                    put("error", "Only admins can perform this action");
                }});
            }
            
            userService.deleteUser(username);
            logger.info("Successfully deleted user {}", username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Error deleting user: " + e.getMessage());
            }});
        }
    }
} 