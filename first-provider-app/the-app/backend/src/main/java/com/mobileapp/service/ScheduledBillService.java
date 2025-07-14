package com.mobileapp.service;

import com.mobileapp.entity.FacturaF1;
import com.mobileapp.model.ScheduledBill;
import com.mobileapp.repository.ScheduledBillRepository;
import com.mobileapp.repository.FacturaF1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ScheduledBillService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledBillService.class);
    private static final ZoneId BUCHAREST_ZONE = ZoneId.of("Europe/Bucharest");
    private static final String DEFAULT_FURNIZOR = "F-Telecom";

    @Autowired
    private ScheduledBillRepository scheduledBillRepository;

    @Autowired
    private FacturaF1Service facturaF1Service;

    @Autowired
    private FacturaF1Repository facturaF1Repository;

    @Autowired
    private WebSocketService webSocketService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processScheduledBills() {
        try {
            LocalDateTime now = LocalDateTime.now(BUCHAREST_ZONE);
            logger.info("Processing scheduled bills at: {}", now);

            List<ScheduledBill> dueBills = scheduledBillRepository.findByStatusAndScheduledDateTimeBefore("PENDING", now);
            logger.info("Found {} scheduled bills to process", dueBills.size());

            for (ScheduledBill scheduledBill : dueBills) {
                try {
                    logger.info("Processing scheduled bill: {}", scheduledBill.getId());

                    FacturaF1 facturaF1 = new FacturaF1();
                    facturaF1.setUsername(scheduledBill.getUsername());
                    facturaF1.setTip(scheduledBill.getTip());
                    facturaF1.setSum(scheduledBill.getSum().doubleValue());
                    facturaF1.setDatasc(scheduledBill.getDatasc());
                    facturaF1.setDatacr(now.toLocalDate());
                    facturaF1.setFurnizor(DEFAULT_FURNIZOR);
                    facturaF1.setPhone(scheduledBill.getPhone());
                    facturaF1.setAddress(scheduledBill.getAddress());
                    facturaF1.setCode(scheduledBill.getCode());
                    facturaF1.setPlatita("NU");

                    FacturaF1 savedFacturaF1 = facturaF1Service.saveFactura(facturaF1);
                    logger.info("Created new bill from scheduled bill using FacturaF1Service: {}", savedFacturaF1.getId());

                    long billCount = facturaF1Repository.count();
                    webSocketService.sendBillCountUpdate(billCount);

                    scheduledBill.setStatus("PROCESSED");
                    scheduledBillRepository.save(scheduledBill);
                    logger.info("Marked scheduled bill as processed: {}", scheduledBill.getId());
                } catch (Exception e) {
                    logger.error("Error processing scheduled bill {}: {}", scheduledBill.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error in processScheduledBills: {}", e.getMessage());
        }
    }
} 