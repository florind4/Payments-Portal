package com.mobileapp.controller;

import com.mobileapp.service.BillNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/bill-notifications")
@CrossOrigin(origins = "*")
public class BillNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(BillNotificationController.class);

    @Autowired
    private BillNotificationService billNotificationService;

    @PostMapping("/user/{username}")
    public ResponseEntity<?> sendNotificationsForUser(@PathVariable String username) {
        try {
            logger.info("Received request to send notifications for user: {}", username);
            billNotificationService.sendNotificationsForUser(username);
            return ResponseEntity.ok("Notifications sent successfully for user: " + username);
        } catch (Exception e) {
            logger.error("Failed to send notifications for user: {}", username, e);
            return ResponseEntity.badRequest().body("Failed to send notifications: " + e.getMessage());
        }
    }

    @PostMapping("/bill/{billId}")
    public ResponseEntity<?> sendNotificationForBill(@PathVariable Long billId) {
        try {
            logger.info("Received request to send notification for bill ID: {}", billId);
            billNotificationService.sendNotificationForBill(billId);
            return ResponseEntity.ok("Notification sent successfully for bill ID: " + billId);
        } catch (Exception e) {
            logger.error("Failed to send notification for bill ID: {}", billId, e);
            return ResponseEntity.badRequest().body("Failed to send notification: " + e.getMessage());
        }
    }

    @PostMapping("/all-unpaid")
    public ResponseEntity<?> sendNotificationsForAllUnpaidBills() {
        try {
            logger.info("Received request to send notifications for all unpaid bills");
            billNotificationService.sendNotificationsForAllUnpaidBills();
            return ResponseEntity.ok("Notifications sent successfully for all unpaid bills");
        } catch (Exception e) {
            logger.error("Failed to send notifications for all unpaid bills", e);
            return ResponseEntity.badRequest().body("Failed to send notifications: " + e.getMessage());
        }
    }
} 