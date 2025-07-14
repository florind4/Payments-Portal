package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.payload.request.PaymentRequest;
import com.mobileapp.payload.response.MessageResponse;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    private static final Pattern CARD_PATTERN = Pattern.compile("^\\d{16}$");
    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$");

    @PostMapping("/card")
    @Transactional
    public ResponseEntity<?> processCardPayment(@RequestHeader("Authorization") String token,
                                              @Valid @RequestBody PaymentRequest request) {
        logger.info("=== CARD PAYMENT REQUEST RECEIVED ===");
        logger.info("Request headers - Authorization: {}", token != null ? "Token exists" : "No token");
        logger.info("Request body - Card number: {}, Amount: {}", request.getCardNumber(), request.getAmount());
        logger.info("Request URL: /payments/card");
        
        try {

            if (token == null || !token.startsWith("Bearer ")) {
                logger.error("Invalid token format");
                return ResponseEntity.status(401)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageResponse("Invalid token format"));
            }

            if (request.getCardNumber() == null || !CARD_PATTERN.matcher(request.getCardNumber()).matches()) {
                logger.error("Invalid card number format");
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageResponse("Invalid card number format. Must be 16 digits."));
            }

            if (request.getAmount() == null || request.getAmount() <= 0) {
                logger.error("Invalid amount");
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageResponse("Amount must be greater than 0"));
            }

            logger.info("Extracting username from token...");
            String username = userService.getUsernameFromToken(token);
            if (username == null) {
                logger.error("Failed to extract username from token");
                return ResponseEntity.status(401)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageResponse("Invalid or expired token"));
            }
            logger.info("Successfully extracted username: {}", username);

            logger.info("Fetching user from database for username: {}", username);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found in database: {}", username);
                        return new RuntimeException("User not found");
                    });
            logger.info("Successfully found user in database: {}", user.getUsername());

            Double currentBalance = user.getBalance();
            if (currentBalance == null) {
                currentBalance = 0.0;
                logger.info("Balance was null, defaulting to 0.0");
            }
            logger.info("Current balance from database for user {}: {}", username, currentBalance);

            Double amount = request.getAmount();
            logger.info("Amount to add: {}", amount);
            Double newBalance = currentBalance + amount;
            logger.info("New balance calculation: {} + {} = {}", currentBalance, amount, newBalance);

            logger.info("Updating balance in database for user {} from {} to {}", username, currentBalance, newBalance);
            user.setBalance(newBalance);
            try {
                UtilizatorF1 savedUser = utilizatorF1Repository.save(user);
                logger.info("Database update successful. New balance: {}", savedUser.getBalance());
            } catch (Exception e) {
                logger.error("Failed to save user balance: {}", e.getMessage());
                throw new RuntimeException("Failed to update balance: " + e.getMessage());
            }
            
            logger.info("=== CARD PAYMENT PROCESSED SUCCESSFULLY ===");
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MessageResponse("Payment processed successfully"));
        } catch (Exception e) {
            logger.error("=== CARD PAYMENT ERROR ===");
            logger.error("Error type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Stack trace:", e);

            if (e instanceof RuntimeException && e.getMessage().contains("User not found")) {
                return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageResponse("User not found"));
            } else if (e instanceof RuntimeException && e.getMessage().contains("Failed to update balance")) {
                return ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageResponse("Failed to update balance: " + e.getMessage()));
            }
            
            return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MessageResponse("Error processing payment: " + e.getMessage()));
        }
    }

    @PostMapping("/bank-transfer")
    @Transactional
    public ResponseEntity<?> processBankTransfer(@RequestHeader("Authorization") String token,
                                               @Valid @RequestBody PaymentRequest request) {
        logger.info("=== BANK TRANSFER REQUEST RECEIVED ===");
        logger.info("Request headers - Authorization: {}", token != null ? "Token exists" : "No token");
        logger.info("Request body - IBAN: {}, Amount: {}", request.getIban(), request.getAmount());
        
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                logger.error("Invalid token format");
                return ResponseEntity.status(401)
                    .body(new MessageResponse("Invalid token format"));
            }

            if (request.getIban() == null || !IBAN_PATTERN.matcher(request.getIban()).matches()) {
                logger.error("Invalid IBAN format");
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid IBAN format"));
            }

            if (request.getAmount() == null || request.getAmount() <= 0) {
                logger.error("Invalid amount");
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Amount must be greater than 0"));
            }

            logger.info("Extracting username from token...");
            String username = userService.getUsernameFromToken(token);
            if (username == null) {
                logger.error("Failed to extract username from token");
                return ResponseEntity.status(401)
                    .body(new MessageResponse("Invalid token"));
            }
            logger.info("Successfully extracted username: {}", username);

            logger.info("Fetching user from database for username: {}", username);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found in database: {}", username);
                        return new RuntimeException("User not found");
                    });
            logger.info("Successfully found user in database: {}", user.getUsername());
            
            Double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
            logger.info("Current balance from database for user {}: {}", username, currentBalance);

            Double amount = request.getAmount();
            logger.info("Amount to add: {}", amount);
            Double newBalance = currentBalance + amount;
            logger.info("New balance calculation: {} + {} = {}", currentBalance, amount, newBalance);

            logger.info("Updating balance in database for user {} from {} to {}", username, currentBalance, newBalance);
            user.setBalance(newBalance);
            UtilizatorF1 savedUser = utilizatorF1Repository.save(user);
            logger.info("Database update successful. New balance: {}", savedUser.getBalance());
            
            logger.info("=== BANK TRANSFER PROCESSED SUCCESSFULLY ===");
            return ResponseEntity.ok(new MessageResponse("Bank transfer processed successfully"));
        } catch (Exception e) {
            logger.error("=== BANK TRANSFER ERROR ===");
            logger.error("Error type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Stack trace:", e);
            return ResponseEntity.status(500)
                .body(new MessageResponse("Error processing bank transfer: " + e.getMessage()));
        }
    }
} 