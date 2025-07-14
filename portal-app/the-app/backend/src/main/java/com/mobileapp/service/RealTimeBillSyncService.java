package com.mobileapp.service;

import com.mobileapp.entity.FacturaF1Sync;
import com.mobileapp.entity.FacturaF2Sync;
import com.mobileapp.model.Bill;
import com.mobileapp.repository.BillRepository;
import com.mobileapp.repository.FacturaF1SyncRepository;
import com.mobileapp.repository.FacturaF2SyncRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RealTimeBillSyncService {
    private static final Logger logger = LoggerFactory.getLogger(RealTimeBillSyncService.class);
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private FacturaF1SyncRepository facturaF1SyncRepository;
    
    @Autowired
    private FacturaF2SyncRepository facturaF2SyncRepository;

    @Scheduled(fixedRate = 12000)
    @Transactional
    public void syncBillStatus() {
        try {
            logger.debug("Starting bill sync process");

            List<Bill> paidBills = billRepository.findByPlatita("DA");
            
            if (paidBills.isEmpty()) {
                logger.debug("No paid bills found, skipping sync");
                return;
            }
            
            logger.debug("Found {} paid bills to sync", paidBills.size());

            syncWithFacturif1(paidBills);

            syncWithFacturif2(paidBills);
            
        } catch (Exception e) {
            logger.error("Error during bill sync: {}", e.getMessage(), e);
        }
    }
    
    private void syncWithFacturif1(List<Bill> paidBills) {
        try {
            List<FacturaF1Sync> unpaidF1Bills = facturaF1SyncRepository.findByPlatita("NU");
            
            if (unpaidF1Bills.isEmpty()) {
                logger.debug("No unpaid bills in facturif1, skipping sync");
                return;
            }
            
            logger.debug("Found {} unpaid bills in facturif1", unpaidF1Bills.size());
            
            int updatedCount = 0;
            
            for (Bill paidBill : paidBills) {
                for (FacturaF1Sync unpaidBill : unpaidF1Bills) {
                    if (matchesBill(paidBill, unpaidBill)) {
                        logger.debug("Updating facturif1 bill ID: {} to paid", unpaidBill.getId());
                        unpaidBill.setPlatita("DA");
                        facturaF1SyncRepository.save(unpaidBill);
                        updatedCount++;
                        break;
                    }
                }
            }
            
            if (updatedCount > 0) {
                logger.info("Facturif1 sync completed: {} bills updated", updatedCount);
            } else {
                logger.debug("Facturif1 sync completed: no updates needed");
            }
            
        } catch (Exception e) {
            logger.error("Error during facturif1 sync: {}", e.getMessage(), e);
        }
    }
    
    private void syncWithFacturif2(List<Bill> paidBills) {
        try {
            List<FacturaF2Sync> unpaidF2Bills = facturaF2SyncRepository.findByPlatita("NU");
            
            if (unpaidF2Bills.isEmpty()) {
                logger.debug("No unpaid bills in facturif2, skipping sync");
                return;
            }
            
            logger.debug("Found {} unpaid bills in facturif2", unpaidF2Bills.size());
            
            int updatedCount = 0;
            
            for (Bill paidBill : paidBills) {
                for (FacturaF2Sync unpaidBill : unpaidF2Bills) {
                    if (matchesBill(paidBill, unpaidBill)) {
                        logger.debug("Updating facturif2 bill ID: {} to paid", unpaidBill.getId());
                        unpaidBill.setPlatita("DA");
                        facturaF2SyncRepository.save(unpaidBill);
                        updatedCount++;
                        break;
                    }
                }
            }
            
            if (updatedCount > 0) {
                logger.info("Facturif2 sync completed: {} bills updated", updatedCount);
            } else {
                logger.debug("Facturif2 sync completed: no updates needed");
            }
            
        } catch (Exception e) {
            logger.error("Error during facturif2 sync: {}", e.getMessage(), e);
        }
    }
    
    private boolean matchesBill(Bill paidBill, FacturaF1Sync unpaidBill) {
        return "NU".equals(unpaidBill.getPlatita()) &&
               paidBill.getTip().equals(unpaidBill.getTip()) &&
               paidBill.getFurnizor().equals(unpaidBill.getFurnizor()) &&
               paidBill.getDatacr().equals(unpaidBill.getDatacr()) &&
               paidBill.getDatasc().equals(unpaidBill.getDatasc()) &&
               paidBill.getPhone().equals(unpaidBill.getPhone()) &&
               paidBill.getSum().doubleValue() == unpaidBill.getSum();
    }
    
    private boolean matchesBill(Bill paidBill, FacturaF2Sync unpaidBill) {
        return "NU".equals(unpaidBill.getPlatita()) &&
               paidBill.getTip().equals(unpaidBill.getTip()) &&
               paidBill.getFurnizor().equals(unpaidBill.getFurnizor()) &&
               paidBill.getDatacr().equals(unpaidBill.getDatacr()) &&
               paidBill.getDatasc().equals(unpaidBill.getDatasc()) &&
               paidBill.getPhone().equals(unpaidBill.getPhone()) &&
               paidBill.getSum().doubleValue() == unpaidBill.getSum();
    }
} 