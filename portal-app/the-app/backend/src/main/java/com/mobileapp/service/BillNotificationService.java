package com.mobileapp.service;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.model.Bill;
import com.mobileapp.repository.BillRepository;
import com.mobileapp.repository.UtilizatorF1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class BillNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(BillNotificationService.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public void sendNotificationsForUser(String username) {
        try {
            logger.info("Sending notifications for all unpaid bills for user: {}", username);

            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            logger.info("Found user: {} (email: {}, phone: {})", username, user.getEmail(), user.getPhone());

            List<Bill> unpaidBills = billRepository.findByUsernameAndPlatita(username, "NU");
            logger.info("Found {} unpaid bills for user: {}", unpaidBills.size(), username);
            
            for (Bill bill : unpaidBills) {
                sendBillNotification(user, bill);
            }
            
            logger.info("Completed sending notifications for user: {}", username);
        } catch (Exception e) {
            logger.error("Failed to send notifications for user: {}", username, e);
            throw new RuntimeException("Failed to send notifications: " + e.getMessage(), e);
        }
    }

    public void sendNotificationForBill(Long billId) {
        try {
            logger.info("Sending notification for bill ID: {}", billId);

            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found: " + billId));
            
            logger.info("Found bill: {} for user: {}", billId, bill.getUsername());

            UtilizatorF1 user = utilizatorF1Repository.findByUsername(bill.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + bill.getUsername()));
            
            logger.info("Found user: {} (email: {}, phone: {})", user.getUsername(), user.getEmail(), user.getPhone());
            
            sendBillNotification(user, bill);
            
            logger.info("Completed sending notification for bill ID: {}", billId);
        } catch (Exception e) {
            logger.error("Failed to send notification for bill ID: {}", billId, e);
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    public void sendNotificationsForAllUnpaidBills() {
        try {
            logger.info("Sending notifications for all unpaid bills in the system");

            List<Bill> unpaidBills = billRepository.findByPlatita("NU");
            logger.info("Found {} unpaid bills in total", unpaidBills.size());
            
            for (Bill bill : unpaidBills) {
                try {
                    UtilizatorF1 user = utilizatorF1Repository.findByUsername(bill.getUsername())
                            .orElse(null);
                    
                    if (user != null) {
                        logger.info("Sending notification for bill ID: {} to user: {}", bill.getId(), user.getUsername());
                        sendBillNotification(user, bill);
                    } else {
                        logger.warn("User not found for bill ID: {} (username: {})", bill.getId(), bill.getUsername());
                    }
                } catch (Exception e) {
                    logger.error("Failed to send notification for bill ID: {}", bill.getId(), e);
                }
            }
            
            logger.info("Completed sending notifications for all unpaid bills");
        } catch (Exception e) {
            logger.error("Failed to send notifications for all unpaid bills", e);
            throw new RuntimeException("Failed to send notifications: " + e.getMessage(), e);
        }
    }

    private void sendBillNotification(UtilizatorF1 user, Bill bill) {
        try {
            String dueDate = bill.getDatasc() != null ? bill.getDatasc().toString() : "Not specified";
            String billType = bill.getTip() != null ? bill.getTip() : "Unknown";

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                logger.info("Sending email notification to user's email: {} for bill ID: {}", user.getEmail(), bill.getId());
                emailService.sendBillNotification(
                    user.getEmail(),
                    user.getUsername(),
                    billType,
                    bill.getSum(),
                    dueDate
                );
            } else {
                logger.warn("User {} has no email address configured, skipping email notification for bill ID: {}", 
                    user.getUsername(), bill.getId());
            }

            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                logger.info("Sending SMS notification to user's phone: {} for bill ID: {}", user.getPhone(), bill.getId());
                smsService.sendBillNotification(
                    user.getPhone(),
                    user.getUsername(),
                    billType,
                    bill.getSum(),
                    dueDate
                );
            } else {
                logger.warn("User {} has no phone number configured, skipping SMS notification for bill ID: {}", 
                    user.getUsername(), bill.getId());
            }
        } catch (Exception e) {
            logger.error("Failed to send notifications for bill ID: {}", bill.getId(), e);
            throw new RuntimeException("Failed to send bill notification: " + e.getMessage(), e);
        }
    }
} 