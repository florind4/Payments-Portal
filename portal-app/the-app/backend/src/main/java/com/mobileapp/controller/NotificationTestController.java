package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.service.EmailService;
import com.mobileapp.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class NotificationTestController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationTestController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @PostMapping("/email")
    public ResponseEntity<?> testEmail(@RequestParam String email, @RequestParam String username) {
        try {
            logger.info("Testing email notification to: {}", email);
            emailService.sendBillNotification(email, username, "Test Bill", 100.0, "2024-01-31");
            return ResponseEntity.ok("Email test sent successfully to: " + email);
        } catch (Exception e) {
            logger.error("Email test failed", e);
            return ResponseEntity.badRequest().body("Email test failed: " + e.getMessage());
        }
    }

    @PostMapping("/sms")
    public ResponseEntity<?> testSms(@RequestParam String phone, @RequestParam String username) {
        try {
            logger.info("Testing SMS notification to: {}", phone);
            smsService.sendBillNotification(phone, username, "Test Bill", 100.0, "2024-01-31");
            return ResponseEntity.ok("SMS test sent successfully to: " + phone);
        } catch (Exception e) {
            logger.error("SMS test failed", e);
            return ResponseEntity.badRequest().body("SMS test failed: " + e.getMessage());
        }
    }

    @PostMapping("/email-payment")
    public ResponseEntity<?> testEmailPayment(@RequestParam String email, @RequestParam String username) {
        try {
            logger.info("Testing email payment confirmation to: {}", email);
            emailService.sendBillPaymentConfirmation(email, username, "Test Bill", 100.0);
            return ResponseEntity.ok("Email payment confirmation test sent successfully to: " + email);
        } catch (Exception e) {
            logger.error("Email payment confirmation test failed", e);
            return ResponseEntity.badRequest().body("Email payment confirmation test failed: " + e.getMessage());
        }
    }

    @PostMapping("/sms-payment")
    public ResponseEntity<?> testSmsPayment(@RequestParam String phone, @RequestParam String username) {
        try {
            logger.info("Testing SMS payment confirmation to: {}", phone);
            smsService.sendBillPaymentConfirmation(phone, username, "Test Bill", 100.0);
            return ResponseEntity.ok("SMS payment confirmation test sent successfully to: " + phone);
        } catch (Exception e) {
            logger.error("SMS payment confirmation test failed", e);
            return ResponseEntity.badRequest().body("SMS payment confirmation test failed: " + e.getMessage());
        }
    }

    @PostMapping("/user-notifications")
    public ResponseEntity<?> testUserNotifications(@RequestParam String username) {
        try {
            logger.info("Testing notifications for user: {}", username);

            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            logger.info("Found user: {} (email: {}, phone: {})", username, user.getEmail(), user.getPhone());

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                logger.info("Sending test email to user's email: {}", user.getEmail());
                String emailSubject = "Test Notificare - F-Portal";
                String emailBody = String.format(
                    "Stimate/ă %s,\n\n" +
                    "Aceasta este o notificare de test pentru a verifica funcționarea sistemului de notificări.\n\n" +
                    "Detalii test:\n" +
                    "• Utilizator: %s\n" +
                    "• Email: %s\n" +
                    "• Telefon: %s\n" +
                    "• Timp test: %s\n\n" +
                    "Dacă primiți această notificare, sistemul de email funcționează corect.\n\n" +
                    "Vă mulțumim!\n\n" +
                    "Cu stimă,\n" +
                    "Echipa F-Portal",
                    user.getUsername(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone() != null ? user.getPhone() : "N/A",
                    java.time.LocalDateTime.now().toString()
                );
                emailService.sendSimpleEmail(user.getEmail(), emailSubject, emailBody);
            } else {
                logger.warn("User {} has no email address configured", username);
            }

            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                logger.info("Sending test WhatsApp to user's phone: {}", user.getPhone());
                String message = String.format(
                    "🧪 TEST NOTIFICARE\n\n" +
                    "Stimate/ă %s,\n\n" +
                    "Aceasta este o notificare de test pentru a verifica funcționarea sistemului de notificări WhatsApp.\n\n" +
                    "📱 Telefon: %s\n" +
                    "📧 Email: %s\n" +
                    "⏰ Timp test: %s\n\n" +
                    "Dacă primiți această notificare, sistemul WhatsApp funcționează corect.\n\n" +
                    "Vă mulțumim!",
                    user.getUsername(),
                    user.getPhone(),
                    user.getEmail() != null ? user.getEmail() : "N/A",
                    java.time.LocalDateTime.now().toString()
                );
                smsService.createMsj(message);
            } else {
                logger.warn("User {} has no phone number configured", username);
            }
            
            return ResponseEntity.ok("Test notifications sent successfully for user: " + username);
        } catch (Exception e) {
            logger.error("Failed to send test notifications", e);
            return ResponseEntity.badRequest().body("Failed to send test notifications: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        try {
            return ResponseEntity.ok("Notification services are ready. Use the test endpoints to verify functionality.");
        } catch (Exception e) {
            logger.error("Status check failed", e);
            return ResponseEntity.badRequest().body("Status check failed: " + e.getMessage());
        }
    }
} 