package com.mobileapp.controller;

import com.mobileapp.dto.CreateBillRequest;
import com.mobileapp.model.Bill;
import com.mobileapp.model.User;
import com.mobileapp.repository.BillRepository;
import com.mobileapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getBills(@RequestParam(required = false) String username) {
        try {
            logger.info("Received request to get bills for username: {}", username);
            List<Bill> bills;
            if (username != null && !username.isEmpty()) {
                bills = billRepository.findByUsername(username);
            } else {
                bills = billRepository.findAll();
            }
            logger.info("Successfully retrieved {} bills", bills.size());
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            logger.error("Error retrieving bills: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving bills: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody CreateBillRequest request) {
        try {
            logger.info("Received createBill request: {}", request);
            
            if (request.getIsScheduled() != null && request.getIsScheduled()) {
                return ResponseEntity.badRequest().body("This endpoint is only for non-scheduled bills. Use /api/scheduled-bills for scheduled bills.");
            }

            User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + request.getUsername()));

            logger.info("Found user: {}", user);

            Bill bill = new Bill();
            bill.setUsername(request.getUsername());
            bill.setTip(request.getTip());
            bill.setSum(request.getSum());
            bill.setDatasc(request.getDatasc());
            bill.setDatacr(LocalDate.now());
            bill.setFurnizor(user.getFirstName() + " " + user.getLastName());
            bill.setPhone(user.getPhoneNumber());
            bill.setAddress(user.getAddress());
            bill.setCode(user.getCode());
            bill.setIsScheduled(false);
            bill.setStatus("GENERATED");

            logger.info("Saving bill: {}", bill);
            Bill savedBill = billRepository.save(bill);
            logger.info("Bill saved successfully: {}", savedBill);
            return ResponseEntity.ok(savedBill);
        } catch (Exception e) {
            logger.error("Error creating bill", e);
            return ResponseEntity.badRequest().body("Error creating bill: " + e.getMessage());
        }
    }
} 