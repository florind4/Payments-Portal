package com.mobileapp.service;

import com.mobileapp.entity.FacturaF1Sync;
import com.mobileapp.entity.FacturaF2Sync;
import com.mobileapp.repository.FacturaF1SyncRepository;
import com.mobileapp.repository.FacturaF2SyncRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacturaF1SyncService {
    private static final Logger logger = LoggerFactory.getLogger(FacturaF1SyncService.class);
    
    @Autowired
    private FacturaF1SyncRepository facturaF1SyncRepository;
    
    @Autowired
    private FacturaF2SyncRepository facturaF2SyncRepository;

    @Transactional
    public void syncBillPayment(String tip, Double sum, String phone, String furnizor) {
        try {
            logger.debug("Syncing bill payment: tip={}, sum={}, phone={}, furnizor={}", 
                tip, sum, phone, furnizor);

            syncWithFacturif1(tip, sum, phone, furnizor);

            syncWithFacturif2(tip, sum, phone, furnizor);
            
            logger.debug("Bill payment sync completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during bill payment sync: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync bill payment: " + e.getMessage(), e);
        }
    }
    
    private void syncWithFacturif1(String tip, Double sum, String phone, String furnizor) {
        try {
            List<FacturaF1Sync> matchingBills = facturaF1SyncRepository.findMatchingUnpaidBills(tip, sum, phone, furnizor);
            
            if (matchingBills.isEmpty()) {
                logger.debug("No matching unpaid bills found in facturif1 for payment sync");
                return;
            }
            
            logger.debug("Found {} matching bills in facturif1 to update", matchingBills.size());

            for (FacturaF1Sync bill : matchingBills) {
                if ("NU".equals(bill.getPlatita())) {
                    logger.debug("Updating facturif1 bill ID: {} to paid", bill.getId());
                    bill.setPlatita("DA");
                    facturaF1SyncRepository.save(bill);
                }
            }
            
            logger.debug("Facturif1 payment sync completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during facturif1 payment sync: {}", e.getMessage(), e);
        }
    }
    
    private void syncWithFacturif2(String tip, Double sum, String phone, String furnizor) {
        try {
            List<FacturaF2Sync> matchingBills = facturaF2SyncRepository.findMatchingUnpaidBills(tip, sum, phone, furnizor);
            
            if (matchingBills.isEmpty()) {
                logger.debug("No matching unpaid bills found in facturif2 for payment sync");
                return;
            }
            
            logger.debug("Found {} matching bills in facturif2 to update", matchingBills.size());

            for (FacturaF2Sync bill : matchingBills) {
                if ("NU".equals(bill.getPlatita())) {
                    logger.debug("Updating facturif2 bill ID: {} to paid", bill.getId());
                    bill.setPlatita("DA");
                    facturaF2SyncRepository.save(bill);
                }
            }
            
            logger.debug("Facturif2 payment sync completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during facturif2 payment sync: {}", e.getMessage(), e);
        }
    }
} 