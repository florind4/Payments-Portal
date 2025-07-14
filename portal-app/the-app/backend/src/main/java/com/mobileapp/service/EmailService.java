package com.mobileapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        try {
            logger.info("Preparing to send email to: {}", toEmail);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ozoneflo@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);
            
            mailSender.send(message);
            
            logger.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }

    public void sendBillNotification(String toEmail, String username, String billType, Double amount, String dueDate) {
        String subject = "Factura noua!";
        String body = String.format(
            "Salut %s,\n\n" +
            "Ai o factura noua:\n" +
            "Tip: %s\n" +
            "Suma: %.2f RON\n" +
            "Data scadenta: %s\n\n" +
            "Te rugam sa verifici platforma pentru detalii.",
            username, billType, amount, dueDate
        );
        sendSimpleEmail(toEmail, subject, body);
    }

    public void sendBillPaymentConfirmation(String toEmail, String username, String billType, Double amount) {
        String subject = "Confirmare plata factura";
        String body = String.format(
            "Salut %s,\n\n" +
            "Plata facturii a fost confirmata:\n" +
            "Tip: %s\n" +
            "Suma: %.2f RON\n\n" +
            "Multumim!",
            username, billType, amount
        );
        sendSimpleEmail(toEmail, subject, body);
    }
} 