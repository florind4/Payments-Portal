package com.mobileapp.service;

import com.mobileapp.entity.Tranzactie;
import com.mobileapp.repository.TranzactieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    @Autowired
    private TranzactieRepository tranzactieRepository;
    
    @Transactional
    public Tranzactie recordBillPayment(Long billId, String username, String billType, Double amount) {
        logger.debug("Recording bill payment transaction: billId={}, username={}, type={}, amount={}", 
            billId, username, billType, amount);
        
        try {
            Tranzactie tranzactie = new Tranzactie();
            tranzactie.setIdFactura(billId);
            tranzactie.setUsername(username);
            tranzactie.setTip(billType != null ? billType : "Unknown");
            tranzactie.setSum(amount);
            tranzactie.setDate(LocalDate.now());
            
            Tranzactie savedTranzactie = tranzactieRepository.save(tranzactie);
            logger.debug("Transaction recorded successfully with ID: {}", savedTranzactie.getId());
            
            return savedTranzactie;
        } catch (Exception e) {
            logger.error("Failed to record transaction: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to record transaction: " + e.getMessage(), e);
        }
    }
    
    public List<Tranzactie> getUserTransactions(String username) {
        logger.debug("Fetching transactions for user: {}", username);
        return tranzactieRepository.findByUsernameOrderByDateDesc(username);
    }
    
    public List<Tranzactie> getAllTransactions() {
        logger.debug("Fetching all transactions");
        return tranzactieRepository.findAll();
    }
    
    /**
     * Get a specific transaction by ID
     * @param transactionId The ID of the transaction
     * @return Optional containing the transaction if found
     */
    public Optional<Tranzactie> getTransactionById(Long transactionId) {
        logger.info("Fetching transaction with ID: {}", transactionId);
        return tranzactieRepository.findById(transactionId);
    }

    /**
     * Get transaction by bill ID
     * @param billId The ID of the bill
     * @return Optional containing the transaction if found
     */
    public Optional<Tranzactie> getTransactionByBillId(Long billId) {
        logger.info("Fetching transaction for bill ID: {}", billId);
        return tranzactieRepository.findByIdFactura(billId);
    }

    /**
     * Get transactions for a specific date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of transactions in the date range
     */
    public List<Tranzactie> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching transactions between {} and {}", startDate, endDate);
        List<Tranzactie> allTransactions = tranzactieRepository.findAll();
        List<Tranzactie> filteredTransactions = allTransactions.stream()
            .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
            .toList();
        logger.info("Found {} transactions in date range", filteredTransactions.size());
        return filteredTransactions;
    }

    /**
     * Get the total count of transactions in the database
     * @return Total number of transactions
     */
    public long getTransactionCount() {
        logger.info("Getting total transaction count");
        long count = tranzactieRepository.count();
        logger.info("Total transaction count: {}", count);
        return count;
    }
} 