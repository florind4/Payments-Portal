package com.mobileapp.controller;

import com.mobileapp.dto.CreateBillRequest;
import com.mobileapp.model.Bill;
import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.BillRepository;
import com.mobileapp.repository.UtilizatorF1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

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

            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found: " + request.getUsername());
            }
            UtilizatorF1 user = userOpt.get();

            logger.info("Found user: {}", user);

            Bill bill = new Bill();
            bill.setUsername(request.getUsername());
            bill.setTip(request.getTip());
            bill.setSum(request.getSum());
            bill.setDatasc(request.getDatasc());
            bill.setDatacr(LocalDate.now());
            bill.setFurnizor("F-Electrica");
            bill.setPhone(user.getPhone());
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