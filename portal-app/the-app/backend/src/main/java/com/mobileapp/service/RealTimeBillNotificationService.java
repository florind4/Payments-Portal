package com.mobileapp.service;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.model.Bill;
import com.mobileapp.repository.BillRepository;
import com.mobileapp.repository.UtilizatorF1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RealTimeBillNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(RealTimeBillNotificationService.class);
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkForNewBills() {
        try {
            logger.info("=== CHECKING FOR NEW UNPAID BILLS ===");

            List<Bill> unpaidBills = billRepository.findByPlatita("NU");
            logger.info("Found {} unpaid bills total", unpaidBills.size());
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneMinuteAgo = now.minusMinutes(1);
            logger.info("Current time: {}", now);
            logger.info("Checking for bills created after: {}", oneMinuteAgo);
            
            int newBillsFound = 0;
            
            for (Bill bill : unpaidBills) {
                if (Boolean.TRUE.equals(bill.getNotificationSent())) {
                    logger.debug("Skipping bill ID: {} - notifications already sent", bill.getId());
                    continue;
                }

                if (bill.getCreatedAt() == null || bill.getCreatedAt().isBefore(oneMinuteAgo)) {
                    logger.debug("Skipping bill ID: {} - not created recently (createdAt: {})", 
                        bill.getId(), bill.getCreatedAt());
                    continue;
                }
                
                newBillsFound++;
                logger.info("Processing NEW unpaid bill - ID: {}, Username: {}, Type: {}, Amount: {}, Created: {}", 
                    bill.getId(), bill.getUsername(), bill.getTip(), bill.getSum(), bill.getCreatedAt());
                
                try {
                    UtilizatorF1 user = utilizatorF1Repository.findByUsername(bill.getUsername())
                            .orElseThrow(() -> new RuntimeException("User not found: " + bill.getUsername()));
                    
                    logger.info("Found user: {} (email: {}, phone: {})", 
                        user.getUsername(), user.getEmail(), user.getPhone());

                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        logger.info("Sending email notification to user's email: {}", user.getEmail());
                        String emailSubject = "Notificare Factură Nouă - F-Portal";
                        String emailContent = String.format(
                            "Stimate %s,\n\n" +
                            "Ați primit o factură nouă pentru serviciul %s în valoare de %.2f RON.\n\n" +
                            "Detalii factură:\n" +
                            "- Tip serviciu: %s\n" +
                            "- Sumă: %.2f RON\n" +
                            "- Data creării: %s\n" +
                            "- Data scadenței: %s\n\n" +
                            "Pentru plată, accesați platforma F-Portal.\n\n" +
                            "Vă mulțumim!\n" +
                            "Echipa F-Portal",
                            user.getUsername(),
                            bill.getTip() != null ? bill.getTip() : "Serviciu telecomunicații",
                            bill.getSum(),
                            bill.getTip() != null ? bill.getTip() : "Serviciu telecomunicații",
                            bill.getSum(),
                            bill.getDatacr() != null ? bill.getDatacr().toString() : "N/A",
                            bill.getDatasc() != null ? bill.getDatasc().toString() : "N/A"
                        );
                        emailService.sendSimpleEmail(user.getEmail(), emailSubject, emailContent);
                    } else {
                        logger.warn("User {} has no email address configured", user.getUsername());
                    }

                    if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                        logger.info("Sending WhatsApp notification to user's phone: {}", user.getPhone());
                        String message = String.format(
                            "🔔 NOTIFICARE FACTURĂ NOUĂ\n\n" +
                            "Stimate/ă %s,\n\n" +
                            "A fost emisă o factură nouă:\n" +
                            "📋 Tip: %s\n" +
                            "💰 Sumă: %.2f RON\n" +
                            "📅 Scadență: %s\n\n" +
                            "Pentru plată, accesați platforma F-Portal.\n\n" +
                            "Vă mulțumim!",
                            user.getUsername(),
                            bill.getTip() != null ? bill.getTip() : "Serviciu telecomunicații",
                            bill.getSum(),
                            bill.getDatasc() != null ? bill.getDatasc().toString() : "N/A"
                        );
                        smsService.createMsj(message);
                    } else {
                        logger.warn("User {} has no phone number configured", user.getUsername());
                    }

                    bill.setNotificationSent(true);
                    billRepository.save(bill);
                    logger.info("Successfully sent notifications for bill ID: {} and marked as notified", bill.getId());
                    
                } catch (Exception e) {
                    logger.error("Failed to process notifications for bill ID: {}", bill.getId(), e);
                }
            }
            
            logger.info("=== COMPLETED CHECKING FOR NEW UNPAID BILLS - Found {} new bills ===", newBillsFound);
        } catch (Exception e) {
            logger.error("=== ERROR CHECKING FOR NEW UNPAID BILLS ===");
            logger.error("Error type: {}", e.getClass().getName());
            logger.error("Error message: {}", e.getMessage());
            logger.error("Stack trace:", e);
        }
    }
} 