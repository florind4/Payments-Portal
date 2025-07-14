package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.payload.request.PasswordUpdateRequest;
import com.mobileapp.payload.request.ProfileUpdateRequest;
import com.mobileapp.payload.response.MessageResponse;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Autowired
    private UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            logger.debug("Retrieved profile for user: {}", username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error getting profile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token, @RequestBody ProfileUpdateRequest request) {
        logger.debug("Processing profile update request");
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                if (!request.getEmail().equals(user.getEmail())) {
                    Optional<UtilizatorF1> existingUserWithEmail = utilizatorF1Repository.findByEmail(request.getEmail());
                    if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(user.getId())) {
                        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken by another user"));
                    }
                }
            }

            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhoneNumber());
            user.setBirthday(request.getBirthday());
            user.setPhoto(request.getPhoto());
            user.setCountry(request.getCountry());
            user.setAddress(request.getAddress());
            user.setPostalCode(request.getPostalCode());
            
            utilizatorF1Repository.save(user);
            logger.info("Profile updated successfully for user: {}", username);

            return ResponseEntity.ok(new MessageResponse("Profile updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update-currency")
    public ResponseEntity<?> updateCurrency(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        try {
            String username = userService.getUsernameFromToken(token);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String currency = request.get("currency");
            if (currency == null || currency.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Currency cannot be empty"));
            }

            user.setCurrency(currency);
            utilizatorF1Repository.save(user);

            return ResponseEntity.ok(new MessageResponse("Currency updated successfully"));
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

            utilizatorF1Repository.save(user);

            return ResponseEntity.ok(new MessageResponse("Password updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
} 