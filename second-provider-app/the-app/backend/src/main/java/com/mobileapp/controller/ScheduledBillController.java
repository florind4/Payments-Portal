package com.mobileapp.controller;

import com.mobileapp.dto.CreateBillRequest;
import com.mobileapp.model.ScheduledBill;
import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.ScheduledBillRepository;
import com.mobileapp.repository.UtilizatorF1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduled-bills")
@CrossOrigin(origins = "*")
public class ScheduledBillController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledBillController.class);
    private static final String DEFAULT_FURNIZOR = "F-Electrica";
    private static final ZoneId BUCHAREST_ZONE = ZoneId.of("Europe/Bucharest");

    @Autowired
    private ScheduledBillRepository scheduledBillRepository;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @PostMapping
    public ResponseEntity<?> createScheduledBill(@RequestBody CreateBillRequest request) {
        try {
            logger.info("Received createScheduledBill request: {}", request);
            
            if (request.getIsScheduled() == null || !request.getIsScheduled()) {
                return ResponseEntity.badRequest().body("This endpoint is only for scheduled bills");
            }
            
            if (request.getScheduledDateTime() == null) {
                return ResponseEntity.badRequest().body("Scheduled date and time are required for scheduled bills");
            }

            LocalDateTime scheduledDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(request.getScheduledDateTime()),
                BUCHAREST_ZONE
            );

            LocalDateTime now = LocalDateTime.now(BUCHAREST_ZONE);
            LocalDateTime bufferTime = now.plusMinutes(1);
            
            logger.info("Scheduled timestamp (UTC): {}", request.getScheduledDateTime());
            logger.info("Scheduled time (Bucharest): {}", scheduledDateTime);
            logger.info("Current time (Bucharest): {}", now);
            logger.info("Buffer time (Bucharest): {}", bufferTime);
            
            if (scheduledDateTime.isBefore(bufferTime)) {
                logger.info("Scheduled time is not in the future. Scheduled: {}, Buffer: {}", scheduledDateTime, bufferTime);
                return ResponseEntity.badRequest().body("Scheduled date and time must be in the future");
            }

            ScheduledBill scheduledBill = new ScheduledBill();
            scheduledBill.setUsername(request.getUsername());
            scheduledBill.setTip(request.getTip());
            scheduledBill.setSum(request.getSum());
            scheduledBill.setDatasc(request.getDatasc());
            scheduledBill.setFurnizor(DEFAULT_FURNIZOR);
            scheduledBill.setPhone(request.getPhone());
            scheduledBill.setAddress(request.getAddress());

            if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
                try {
                    scheduledBill.setCode(Integer.parseInt(request.getCode()));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid code format: {}", request.getCode());
                    scheduledBill.setCode(null);
                }
            } else {
                scheduledBill.setCode(null);
            }
            
            scheduledBill.setScheduledDateTime(scheduledDateTime);
            scheduledBill.setStatus("PENDING");

            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(request.getUsername());
            if (userOpt.isPresent()) {
                UtilizatorF1 user = userOpt.get();
                scheduledBill.setPhone(user.getPhone());
                scheduledBill.setAddress(user.getAddress());
                scheduledBill.setCode(user.getCode());
            }

            logger.info("Saving scheduled bill with tip value: {}", request.getTip());
            ScheduledBill savedScheduledBill = scheduledBillRepository.save(scheduledBill);
            logger.info("Scheduled bill saved successfully: {}", savedScheduledBill);
            return ResponseEntity.ok(savedScheduledBill);
        } catch (Exception e) {
            logger.error("Error creating scheduled bill", e);
            return ResponseEntity.badRequest().body("Error creating scheduled bill: " + e.getMessage());
        }
    }
} 