package com.mobileapp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void createMsj(String msj) {
        try {
            initTwilio();
            
            logger.info("Sending WhatsApp message: {}", msj);

            Message message = Message.creator(
                new com.twilio.type.PhoneNumber("whatsapp:+40753780324"),
                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                "NOTIFICARE: " + msj + " Verificati platforma ! "
            ).create();
            
            logger.info("WhatsApp message sent successfully with SID: {}", message.getSid());
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message", e);
            throw new RuntimeException("WhatsApp sending failed: " + e.getMessage(), e);
        }
    }

    public void sendBillNotification(String toPhoneNumber, String username, String billType, Double amount, String dueDate) {
        String message = String.format("Factura Noua! Username: %s, Tip: %s, Suma: %.2f RON, Data scadenta: %s", 
            username, billType, amount, dueDate);
        createMsj(message);
    }

    public void sendBillPaymentConfirmation(String toPhoneNumber, String username, String billType, Double amount) {
        String message = String.format("Plata Confirmata! Username: %s, Tip: %s, Suma: %.2f RON", 
            username, billType, amount);
        createMsj(message);
    }
} 