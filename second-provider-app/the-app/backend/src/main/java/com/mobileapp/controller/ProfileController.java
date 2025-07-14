package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.payload.request.PasswordUpdateRequest;
import com.mobileapp.payload.request.ProfileUpdateRequest;
import com.mobileapp.payload.response.MessageResponse;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "ProfileController is working!");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            if (userOpt.isPresent()) {
                UtilizatorF1 user = userOpt.get();

                Map<String, Object> profileData = new HashMap<>();
                profileData.put("id", user.getId());
                profileData.put("username", user.getUsername());
                profileData.put("email", user.getEmail());
                profileData.put("firstName", user.getFirstName() != null ? user.getFirstName() : "");
                profileData.put("lastName", user.getLastName() != null ? user.getLastName() : "");

                profileData.put("phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
                profileData.put("birthday", user.getBirthday() != null ? user.getBirthday().toString() : null);
                profileData.put("photo", user.getPhoto());
                profileData.put("country", user.getCountry() != null ? user.getCountry() : "");
                profileData.put("address", user.getAddress());
                profileData.put("postalCode", user.getPostalCode());
                profileData.put("balance", user.getBalance() != null ? user.getBalance().doubleValue() : 0.0);
                profileData.put("admin", "DA".equals(user.getAdmin()));
                profileData.put("code", user.getCode());
                profileData.put("currency", user.getCurrency() != null ? user.getCurrency() : "RON");

                profileData.put("phone", user.getPhone() != null ? user.getPhone() : user.getPhoneNumber());
                profileData.put("isAdmin", "DA".equals(user.getAdmin()));

                return ResponseEntity.ok(profileData);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting profile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token, @RequestBody ProfileUpdateRequest request) {
        logger.debug("Processing profile update request with data: {}", request);
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("Current user data before update: firstName='{}', lastName='{}', birthday={}", 
                user.getFirstName(), user.getLastName(), user.getBirthday());

            if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
                user.setFirstName(request.getFirstName().trim());
                logger.info("Updated firstName to: {}", request.getFirstName().trim());
            } else if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {

                user.setFirstName("User");
                logger.info("Set default firstName: User");
            }
            
            if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
                user.setLastName(request.getLastName().trim());
                logger.info("Updated lastName to: {}", request.getLastName().trim());
            } else if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {

                user.setLastName("Name");
                logger.info("Set default lastName: Name");
            }
            
            if (request.getPhoneNumber() != null) {
                String completePhoneNumber = request.getPhoneNumber().trim();
                user.setPhoneNumber(completePhoneNumber);
                user.setPhone(completePhoneNumber);
                logger.info("Updated phoneNumber to: '{}' (complete with country code)", completePhoneNumber);
            }
            
            if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
                logger.info("Updated address to: {}", request.getAddress());
            }
            
            if (request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode());
                logger.info("Updated postalCode to: {}", request.getPostalCode());
            }
            
            if (request.getCountry() != null) {
                user.setCountry(request.getCountry());
                logger.info("Updated country to: {}", request.getCountry());
            }

            if (request.getBirthday() != null && !request.getBirthday().trim().isEmpty()) {
                try {
                    LocalDate birthday = LocalDate.parse(request.getBirthday());
                    user.setBirthday(birthday);
                    logger.info("Updated birthday to: {}", birthday);
                } catch (Exception e) {
                    logger.warn("Invalid birthday format: {}", request.getBirthday());
                    user.setBirthday(LocalDate.of(1990, 1, 1));
                    logger.info("Set default birthday: 1990-01-01");
                }
            } else if (user.getBirthday() == null) {
                user.setBirthday(LocalDate.of(1990, 1, 1));
                logger.info("Set default birthday: 1990-01-01");
            }
            
            if (request.getPhoto() != null) {
                user.setPhoto(request.getPhoto());
                logger.info("Updated photo");
            }

            if (user.getCurrency() == null || user.getCurrency().trim().isEmpty()) {
                user.setCurrency("RON");
                logger.info("Set default currency: RON");
            }

            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
                user.setFirstName("User");
                logger.info("Set default firstName: User");
            }
            
            if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                user.setLastName("Name");
                logger.info("Set default lastName: Name");
            }
            
            if (user.getBirthday() == null) {
                user.setBirthday(LocalDate.of(1990, 1, 1));
                logger.info("Set default birthday: 1990-01-01");
            }
            
            if (user.getCurrency() == null || user.getCurrency().trim().isEmpty()) {
                user.setCurrency("RON");
                logger.info("Set default currency: RON");
            }
            
            if (user.getAdmin() == null || user.getAdmin().trim().isEmpty()) {
                user.setAdmin("NU");
                logger.info("Set default admin: NU");
            }

            logger.info("Final user data before save: firstName='{}', lastName='{}', birthday={}, currency='{}', admin='{}'", 
                user.getFirstName(), user.getLastName(), user.getBirthday(), user.getCurrency(), user.getAdmin());
            
            try {
                logger.info("About to save user to database...");
                utilizatorF1Repository.save(user);
                logger.info("Database save completed successfully");
            } catch (Exception saveException) {
                logger.error("Database save error type: {}", saveException.getClass().getSimpleName());
                logger.error("Database save error message: {}", saveException.getMessage());
                logger.error("Database save error details:", saveException);

                if (saveException.getMessage() != null && saveException.getMessage().contains("constraint")) {
                    throw new RuntimeException("Database constraint violation: " + saveException.getMessage());
                }

                if (saveException.getMessage() != null && saveException.getMessage().contains("sequence")) {
                    throw new RuntimeException("Database sequence error: " + saveException.getMessage());
                }
                
                throw new RuntimeException("Database save failed: " + saveException.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update-currency")
    public ResponseEntity<?> updateCurrency(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newCurrency = request.get("currency");
            if (newCurrency != null && !newCurrency.trim().isEmpty()) {
                user.setCurrency(newCurrency.toUpperCase());
                utilizatorF1Repository.save(user);
                logger.info("Currency updated to {} for user: {}", newCurrency, username);
                return ResponseEntity.ok(new MessageResponse("Currency updated successfully"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Currency is required"));
            }
        } catch (Exception e) {
            logger.error("Error updating currency: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody PasswordUpdateRequest request) {
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!userService.verifyPassword(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Current password is incorrect"));
            }

            user.setPassword(userService.encodePassword(request.getNewPassword()));
            utilizatorF1Repository.save(user);

            return ResponseEntity.ok(new MessageResponse("Password updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/test-update")
    public ResponseEntity<?> testUpdate(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("Testing update for user: {}", username);
            logger.info("Current user data: firstName='{}', lastName='{}', birthday={}, currency='{}', admin='{}'", 
                user.getFirstName(), user.getLastName(), user.getBirthday(), user.getCurrency(), user.getAdmin());

            user.setFirstName("TestUser");
            user.setLastName("TestName");
            
            logger.info("About to save user with: firstName='{}', lastName='{}'", user.getFirstName(), user.getLastName());
            
            utilizatorF1Repository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test update successful");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Test update error: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-db")
    public ResponseEntity<?> testDatabase(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            if (userOpt.isPresent()) {
                UtilizatorF1 user = userOpt.get();
                response.put("status", "OK");
                response.put("message", "Database connection successful");
                response.put("user", user);
                response.put("firstName", user.getFirstName());
                response.put("lastName", user.getLastName());
                response.put("birthday", user.getBirthday());
                response.put("currency", user.getCurrency());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "ERROR");
                response.put("message", "User not found");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Database test error: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Database test failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/test-phone")
    public ResponseEntity<?> testPhoneUpdate(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String testPhoneNumber = request.get("phoneNumber");
            if (testPhoneNumber != null) {
                logger.info("Testing phone number update for user: {}", username);
                logger.info("Current phone_number: '{}', phone: '{}'", user.getPhoneNumber(), user.getPhone());

                user.setPhoneNumber(testPhoneNumber);
                user.setPhone(testPhoneNumber);
                
                logger.info("Setting phone_number to: '{}'", testPhoneNumber);
                utilizatorF1Repository.save(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Phone number test successful");
                response.put("phoneNumber", user.getPhoneNumber());
                response.put("phone", user.getPhone());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("phoneNumber is required");
            }
        } catch (Exception e) {
            logger.error("Phone test error: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 