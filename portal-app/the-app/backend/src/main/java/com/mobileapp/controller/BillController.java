package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.entity.Tranzactie;
import com.mobileapp.model.Bill;
import com.mobileapp.payload.response.MessageResponse;
import com.mobileapp.repository.BillRepository;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.repository.TranzactieRepository;
import com.mobileapp.service.UserService;
import com.mobileapp.service.FacturaF1SyncService;
import com.mobileapp.service.EmailService;
import com.mobileapp.service.SmsService;
import com.mobileapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {
    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Autowired
    private TranzactieRepository tranzactieRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FacturaF1SyncService facturaF1SyncService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody Bill bill) {
        try {
            logger.info("Received createBill request: {}", bill);
            UtilizatorF1 user = utilizatorF1Repository.findByUsername(bill.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + bill.getUsername()));

            logger.info("Found user: {}", user);

            bill.setPhone(user.getPhone());
            bill.setAddress(user.getAddress());
            bill.setCode(user.getCode());
            bill.setDatacr(LocalDate.now());
            bill.setCreatedAt(LocalDateTime.now());
            bill.setPlatita("NU");

            logger.info("Saving bill: {}", bill);
            Bill savedBill = billRepository.save(bill);
            logger.info("Bill saved successfully: {}", savedBill);

            try {
                logger.info("Preparing to send notifications to user: {} (email: {}, phone: {})", 
                    user.getUsername(), user.getEmail(), user.getPhone());

                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    logger.info("Sending email notification to user's email: {}", user.getEmail());
                    emailService.sendSimpleEmail(
                        user.getEmail(),
                        "Factura noua!",
                        String.format("Factura Noua! Username: %s, Tip: %s, Suma: %.2f RON", 
                            user.getUsername(), bill.getTip(), bill.getSum())
                    );
                } else {
                    logger.warn("User {} has no email address configured", user.getUsername());
                }

                if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                    logger.info("Sending WhatsApp notification to user's phone: {}", user.getPhone());
                    String message = String.format("Factura Noua! Username: %s, Tip: %s, Suma: %.2f RON", 
                        user.getUsername(), bill.getTip(), bill.getSum());
                    smsService.createMsj(message);
                } else {
                    logger.warn("User {} has no phone number configured", user.getUsername());
                }
            } catch (Exception e) {
                logger.error("Failed to send notifications for bill: {}", savedBill.getId(), e);
            }

            return ResponseEntity.ok(savedBill);
        } catch (Exception e) {
            logger.error("Error creating bill", e);
            return ResponseEntity.badRequest().body("Error creating bill: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getBills(@RequestParam(required = false) String username) {
        try {
            List<Bill> bills;
            if (username != null && !username.isEmpty()) {
                logger.info("Received request to get bills for username: {}", username);
                bills = billRepository.findByUsername(username);
            } else {
                logger.info("Received request to get all bills");
                bills = billRepository.findAll();
            }
            logger.info("Successfully retrieved {} bills", bills.size());
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            logger.error("Error retrieving bills: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving bills: " + e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredBills(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String provider) {
        try {
            List<Bill> bills;

            if (username != null && !username.isEmpty()) {
                if (maxPrice != null && provider != null && !provider.isEmpty()) {
                    logger.info("Filtering bills for user: {}, maxPrice: {}, provider: {}", username, maxPrice, provider);
                    bills = billRepository.findByUsernameAndMaxPriceAndProvider(username, maxPrice, provider);
                } else if (maxPrice != null) {
                    logger.info("Filtering bills for user: {}, maxPrice: {}", username, maxPrice);
                    bills = billRepository.findByUsernameAndMaxPrice(username, maxPrice);
                } else if (provider != null && !provider.isEmpty()) {
                    logger.info("Filtering bills for user: {}, provider: {}", username, provider);
                    bills = billRepository.findByUsernameAndProvider(username, provider);
                } else {
                    logger.info("Getting all bills for user: {}", username);
                    bills = billRepository.findByUsername(username);
                }
            } else {
                if (maxPrice != null && provider != null && !provider.isEmpty()) {
                    logger.info("Filtering all bills by maxPrice: {}, provider: {}", maxPrice, provider);
                    bills = billRepository.findByMaxPriceAndProvider(maxPrice, provider);
                } else if (maxPrice != null) {
                    logger.info("Filtering all bills by maxPrice: {}", maxPrice);
                    bills = billRepository.findByMaxPrice(maxPrice);
                } else if (provider != null && !provider.isEmpty()) {
                    logger.info("Filtering all bills by provider: {}", provider);
                    bills = billRepository.findByProvider(provider);
                } else {
                    logger.info("Getting all bills");
                    bills = billRepository.findAll();
                }
            }
            
            logger.info("Successfully retrieved {} filtered bills", bills.size());
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            logger.error("Error retrieving filtered bills: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving filtered bills: " + e.getMessage());
        }
    }

    @PostMapping("/{billId}/pay")
    @Transactional
    public ResponseEntity<?> payBill(@RequestHeader("Authorization") String token,
                                   @PathVariable Long billId) {
        logger.info("Processing bill payment for ID: {}", billId);
        
        try {
            String username = userService.getUsernameFromToken(token);
            logger.debug("Processing payment for user: {}", username);

            UtilizatorF1 user = utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found in database: {}", username);
                        return new RuntimeException("User not found");
                    });

            Bill bill = billRepository.findById(billId)
                    .orElseThrow(() -> {
                        logger.error("Bill not found in database: {}", billId);
                        return new RuntimeException("Bill not found");
                    });

            if (!bill.getUsername().equals(username)) {
                logger.error("Bill does not belong to user: {}", username);
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Bill does not belong to user"));
            }

            if ("DA".equals(bill.getPlatita())) {
                logger.error("Bill is already paid: {}", billId);
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Bill is already paid"));
            }

            Double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
            logger.debug("Current balance for user {}: {}", username, currentBalance);

            if (currentBalance < bill.getSum()) {
                logger.error("Insufficient balance for user {}: {} < {}", username, currentBalance, bill.getSum());
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Insufficient balance"));
            }

            Double newBalance = currentBalance - bill.getSum();
            logger.debug("New balance calculation: {} - {} = {}", currentBalance, bill.getSum(), newBalance);

            user.setBalance(newBalance);
            utilizatorF1Repository.save(user);

            bill.setPlatita("DA");
            billRepository.save(bill);

            logger.debug("Recording transaction for bill payment: ID={}, Username={}, Type={}, Amount={}", 
                billId, username, bill.getTip(), bill.getSum());
            
            try {
                Tranzactie savedTranzactie = transactionService.recordBillPayment(
                    billId,
                    username,
                    bill.getTip(),
                    bill.getSum()
                );
                logger.debug("Transaction recorded successfully: {}", savedTranzactie.getId());
            } catch (Exception e) {
                logger.error("Failed to record transaction: {}", e.getMessage());
            }

            logger.debug("Syncing with facturif1 table");
            facturaF1SyncService.syncBillPayment(
                bill.getTip(),
                bill.getSum(),
                bill.getPhone(),
                bill.getFurnizor() != null ? bill.getFurnizor() : "f-telecom"
            );

            try {
                logger.debug("Sending payment confirmation notifications");

                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    emailService.sendBillPaymentConfirmation(
                        user.getEmail(),
                        user.getUsername(),
                        bill.getTip() != null ? bill.getTip() : "Unknown",
                        bill.getSum()
                    );
                }

                if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                    smsService.sendBillPaymentConfirmation(
                        user.getPhone(),
                        user.getUsername(),
                        bill.getTip() != null ? bill.getTip() : "Unknown",
                        bill.getSum()
                    );
                }
            } catch (Exception e) {
                logger.error("Failed to send payment confirmation notifications: {}", e.getMessage());
            }
            
            logger.info("Bill payment completed successfully for user: {}", username);
            return ResponseEntity.ok(new MessageResponse("Bill paid successfully"));
        } catch (Exception e) {
            logger.error("Bill payment failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
} 