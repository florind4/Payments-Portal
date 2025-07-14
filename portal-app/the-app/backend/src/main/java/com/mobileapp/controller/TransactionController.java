package com.mobileapp.controller;

import com.mobileapp.entity.Tranzactie;
import com.mobileapp.service.TransactionService;
import com.mobileapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserTransactions(@RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to get user transactions");
            String username = userService.getUsernameFromToken(token);
            logger.info("Getting transactions for user: {}", username);
            
            List<Tranzactie> transactions = transactionService.getUserTransactions(username);
            logger.info("Successfully retrieved {} transactions for user: {}", transactions.size(), username);
            
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error retrieving user transactions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving transactions: " + e.getMessage());
        }
    }


    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@RequestHeader("Authorization") String token,
                                               @PathVariable Long transactionId) {
        try {
            logger.info("Received request to get transaction by ID: {}", transactionId);
            String username = userService.getUsernameFromToken(token);
            
            var transactionOpt = transactionService.getTransactionById(transactionId);
            if (transactionOpt.isEmpty()) {
                logger.warn("Transaction not found with ID: {}", transactionId);
                return ResponseEntity.notFound().build();
            }
            
            Tranzactie transaction = transactionOpt.get();

            if (!transaction.getUsername().equals(username)) {
                logger.warn("Transaction {} does not belong to user: {}", transactionId, username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            logger.info("Successfully retrieved transaction: {}", transactionId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            logger.error("Error retrieving transaction {}: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving transaction: " + e.getMessage());
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getTransactionsByDateRange(@RequestHeader("Authorization") String token,
                                                       @RequestParam String startDate,
                                                       @RequestParam String endDate) {
        try {
            logger.info("Received request to get transactions between {} and {}", startDate, endDate);
            String username = userService.getUsernameFromToken(token);
            
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            List<Tranzactie> transactions = transactionService.getTransactionsByDateRange(start, end);

            List<Tranzactie> userTransactions = transactions.stream()
                .filter(t -> t.getUsername().equals(username))
                .toList();
            
            logger.info("Successfully retrieved {} transactions for user {} in date range", 
                userTransactions.size(), username);
            
            return ResponseEntity.ok(userTransactions);
        } catch (Exception e) {
            logger.error("Error retrieving transactions by date range: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving transactions: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> checkDatabaseHealth() {
        try {
            logger.info("=== DATABASE HEALTH CHECK ===");
            long transactionCount = transactionService.getTransactionCount();
            logger.info("Database health check successful. Transaction count: {}", transactionCount);
            
            return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "transactionCount", transactionCount,
                "message", "Database connection and transaction table are working correctly"
            ));
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "unhealthy",
                "error", e.getMessage(),
                "message", "Database connection or transaction table has issues"
            ));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> testTransactionCreation(@RequestHeader("Authorization") String token) {
        try {
            logger.info("=== TESTING TRANSACTION CREATION ===");
            String username = userService.getUsernameFromToken(token);
            logger.info("Testing transaction creation for user: {}", username);

            Tranzactie testTranzactie = transactionService.recordBillPayment(
                999L,
                username,
                "Test Transaction",
                100.0
            );
            
            logger.info("Test transaction created successfully: {}", testTranzactie);
            return ResponseEntity.ok(testTranzactie);
        } catch (Exception e) {
            logger.error("Test transaction creation failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Test failed: " + e.getMessage());
        }
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<?> getTransactionByBillId(@RequestHeader("Authorization") String token,
                                                   @PathVariable Long billId) {
        try {
            logger.info("Received request to get transaction for bill ID: {}", billId);
            String username = userService.getUsernameFromToken(token);
            
            var transactionOpt = transactionService.getTransactionByBillId(billId);
            if (transactionOpt.isEmpty()) {
                logger.warn("No transaction found for bill ID: {}", billId);
                return ResponseEntity.notFound().build();
            }
            
            Tranzactie transaction = transactionOpt.get();

            if (!transaction.getUsername().equals(username)) {
                logger.warn("Transaction for bill {} does not belong to user: {}", billId, username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            logger.info("Successfully retrieved transaction for bill: {}", billId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            logger.error("Error retrieving transaction for bill {}: {}", billId, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving transaction: " + e.getMessage());
        }
    }
} 