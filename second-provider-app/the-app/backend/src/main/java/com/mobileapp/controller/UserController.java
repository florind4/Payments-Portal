package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "UserController is working!");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getUserBalance(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            Double balance = userService.getUserBalance(username);
            Map<String, Double> response = new HashMap<>();
            response.put("balance", balance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting user balance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error getting user balance: " + e.getMessage());
        }
    }

    @GetMapping("/admin-status")
    public ResponseEntity<?> getAdminStatus(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            boolean isAdmin = userService.isUserAdmin(username);
            Map<String, Boolean> response = new HashMap<>();
            response.put("isAdmin", isAdmin);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking admin status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error checking admin status: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            if (!userService.isUserAdmin(username)) {
                return ResponseEntity.status(403).body("Only admins can access this endpoint");
            }
            List<UtilizatorF1> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error getting users: " + e.getMessage());
        }
    }

    @PutMapping("/{username}/admin")
    public ResponseEntity<?> updateUserAdminStatus(
            @PathVariable String username,
            @RequestBody Map<String, Boolean> request,
            @RequestHeader("Authorization") String token) {
        try {
            String adminUsername = userService.getUsernameFromToken(token);
            if (!userService.isUserAdmin(adminUsername)) {
                return ResponseEntity.status(403).body("Only admins can perform this action");
            }
            userService.updateUserAdminStatus(username, request.get("isAdmin"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating user admin status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error updating admin status: " + e.getMessage());
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable String username,
            @RequestBody Map<String, String> updates,
            @RequestHeader("Authorization") String token) {
        try {
            String adminUsername = userService.getUsernameFromToken(token);
            if (!userService.isUserAdmin(adminUsername)) {
                return ResponseEntity.status(403).body("Only admins can perform this action");
            }
            userService.updateUserDetails(username, updates);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating user details: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error updating user details: " + e.getMessage());
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        try {
            String adminUsername = userService.getUsernameFromToken(token);
            if (!userService.isUserAdmin(adminUsername)) {
                return ResponseEntity.status(403).body("Only admins can perform this action");
            }
            userService.deleteUser(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error deleting user: " + e.getMessage());
        }
    }

    @PostMapping("/add-funds")
    public ResponseEntity<?> addFunds(@RequestHeader("Authorization") String token, @RequestBody Map<String, Double> request) {
        try {
            String username = userService.getUsernameFromToken(token);
            Double amount = request.get("amount");
            
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            
            userService.addFunds(username, amount);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error adding funds: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error adding funds: " + e.getMessage());
        }
    }

    @GetMapping("/currency")
    public ResponseEntity<?> getUserCurrency(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            if (userOpt.isPresent()) {
                UtilizatorF1 user = userOpt.get();
                String currency = user.getCurrency() != null ? user.getCurrency() : "RON";
                Map<String, String> response = new HashMap<>();
                response.put("currency", currency);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("currency", "RON"); // Default currency
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Error getting user currency: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error getting user currency: " + e.getMessage());
        }
    }

    @GetMapping("/data")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            if (userOpt.isPresent()) {
                UtilizatorF1 user = userOpt.get();
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting user data: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error getting user data: " + e.getMessage());
        }
    }
} 