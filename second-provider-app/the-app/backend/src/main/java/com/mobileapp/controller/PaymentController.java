package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    public PaymentController() {
        logger.info("=== PAYMENT CONTROLLER INSTANTIATED ===");
    }

    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        logger.info("=== TEST ENDPOINT CALLED ===");
        Map<String, String> response = new HashMap<>();
        response.put("message", "PaymentController is working!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/card")
    @Transactional
    public ResponseEntity<?> processCardPayment(@RequestHeader("Authorization") String token,
                                              @RequestBody Map<String, Object> request) {
        logger.info("=== CARD PAYMENT REQUEST RECEIVED ===");
        logger.info("Request body: {}", request);
        
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                logger.error("Invalid token format");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid token format");
                return ResponseEntity.status(401).body(errorResponse);
            }

            String cardNumber = (String) request.get("cardNumber");
            if (cardNumber == null || cardNumber.length() != 16) {
                logger.error("Invalid card number format");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid card number format. Must be 16 digits.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Object amountObj = request.get("amount");
            if (amountObj == null) {
                logger.error("Amount is null");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Amount is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Double amount;
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            } else {
                amount = Double.parseDouble(amountObj.toString());
            }
            
            if (amount <= 0) {
                logger.error("Invalid amount");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Amount must be greater than 0");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String username = userService.getUsernameFromToken(token);
            if (username == null) {
                logger.error("Failed to extract username from token");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid or expired token");
                return ResponseEntity.status(401).body(errorResponse);
            }
            logger.info("Successfully extracted username: {}", username);

            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            if (userOpt.isEmpty()) {
                logger.error("User not found in database: {}", username);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            UtilizatorF1 user = userOpt.get();
            Integer currentBalance = user.getBalance() != null ? user.getBalance() : 0;
            logger.info("Current balance from database for user {}: {}", username, currentBalance);

            Integer newBalance = currentBalance + amount.intValue();
            logger.info("New balance calculation: {} + {} = {}", currentBalance, amount.intValue(), newBalance);

            user.setBalance(newBalance);
            utilizatorF1Repository.save(user);
            logger.info("Database update successful. New balance: {}", user.getBalance());
            
            logger.info("=== CARD PAYMENT PROCESSED SUCCESSFULLY ===");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Card payment processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("=== CARD PAYMENT ERROR ===");
            logger.error("Error message: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error processing payment: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/bank-transfer")
    @Transactional
    public ResponseEntity<?> processBankTransfer(@RequestHeader("Authorization") String token,
                                               @RequestBody Map<String, Object> request) {
        logger.info("=== BANK TRANSFER REQUEST RECEIVED ===");
        logger.info("Request body: {}", request);
        
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                logger.error("Invalid token format");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid token format");
                return ResponseEntity.status(401).body(errorResponse);
            }

            String iban = (String) request.get("iban");
            if (iban == null || iban.length() < 5) {
                logger.error("Invalid IBAN format");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid IBAN format");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Object amountObj = request.get("amount");
            if (amountObj == null) {
                logger.error("Amount is null");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Amount is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Double amount;
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            } else {
                amount = Double.parseDouble(amountObj.toString());
            }
            
            if (amount <= 0) {
                logger.error("Invalid amount");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Amount must be greater than 0");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String username = userService.getUsernameFromToken(token);
            if (username == null) {
                logger.error("Failed to extract username from token");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid token");
                return ResponseEntity.status(401).body(errorResponse);
            }
            logger.info("Successfully extracted username: {}", username);

            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            if (userOpt.isEmpty()) {
                logger.error("User not found in database: {}", username);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            UtilizatorF1 user = userOpt.get();
            Integer currentBalance = user.getBalance() != null ? user.getBalance() : 0;
            logger.info("Current balance from database for user {}: {}", username, currentBalance);

            Integer newBalance = currentBalance + amount.intValue();
            logger.info("New balance calculation: {} + {} = {}", currentBalance, amount.intValue(), newBalance);

            user.setBalance(newBalance);
            utilizatorF1Repository.save(user);
            logger.info("Database update successful. New balance: {}", user.getBalance());
            
            logger.info("=== BANK TRANSFER PROCESSED SUCCESSFULLY ===");
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bank transfer processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("=== BANK TRANSFER ERROR ===");
            logger.error("Error message: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error processing bank transfer: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
} 