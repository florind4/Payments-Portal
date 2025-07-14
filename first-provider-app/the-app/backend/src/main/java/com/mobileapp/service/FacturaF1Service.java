package com.mobileapp.service;

import com.mobileapp.entity.FacturaF1;
import com.mobileapp.entity.Factura;
import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.entity.Tranzactie;
import com.mobileapp.model.ConexiuneF1;
import com.mobileapp.repository.FacturaF1Repository;
import com.mobileapp.repository.FacturaRepository;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.repository.ConexiuneF1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaF1Service {
    private static final Logger logger = LoggerFactory.getLogger(FacturaF1Service.class);
    
    @Autowired
    private FacturaF1Repository facturaF1Repository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Autowired
    private ConexiuneF1Repository conexiuneF1Repository;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    public FacturaF1 saveFactura(FacturaF1 facturaF1) {
        logger.info("Starting saveFactura process for username: {}", facturaF1.getUsername());
        logger.info("Bill details - Furnizor: {}, Sum: {}, Datacr: {}", 
            facturaF1.getFurnizor(), facturaF1.getSum(), facturaF1.getDatacr());

        FacturaF1 savedFacturaF1 = facturaF1Repository.save(facturaF1);
        logger.info("Saved bill to facturaf1 table with id: {}", savedFacturaF1.getId());

        List<ConexiuneF1> connections = conexiuneF1Repository.findAll();
        logger.info("Found {} total connections", connections.size());

        Optional<ConexiuneF1> conexiune = connections.stream()
            .filter(c -> c.getUsername().equals(facturaF1.getUsername()))
            .findFirst();
        
        if (conexiune.isPresent()) {
            ConexiuneF1 foundConexiune = conexiune.get();
            logger.info("Found conexiune - ID: {}, Username: {}, Dev: {}, Portalname: {}", 
                foundConexiune.getId(), 
                foundConexiune.getUsername(), 
                foundConexiune.getDev(), 
                foundConexiune.getPortalname());

            Factura factura = new Factura();
            factura.setUsername(foundConexiune.getPortalname());
            factura.setSum(facturaF1.getSum());
            factura.setDatacr(facturaF1.getDatacr());
            factura.setDatasc(facturaF1.getDatasc());
            factura.setFurnizor(facturaF1.getFurnizor());
            factura.setPlatita(facturaF1.getPlatita());
            factura.setCode(facturaF1.getCode());
            factura.setAddress(facturaF1.getAddress());
            factura.setTip(facturaF1.getTip());
            factura.setPhone(facturaF1.getPhone());

            try {
                Factura savedFactura = facturaRepository.save(factura);
                logger.info("Successfully saved bill to facturi table with ID: {} and portal username: {}", 
                    savedFactura.getId(), foundConexiune.getPortalname());
            } catch (Exception e) {
                logger.error("Error saving to facturi table: {}", e.getMessage(), e);
                throw e;
            }
        } else {
            logger.info("No conexiune found for username: {}, skipping facturi table", 
                facturaF1.getUsername());

            logger.info("All existing connections:");
            for (ConexiuneF1 conn : connections) {
                logger.info("Connection - ID: {}, Username: {}, Dev: {}, Portalname: {}", 
                    conn.getId(), conn.getUsername(), conn.getDev(), conn.getPortalname());
            }
        }

        return savedFacturaF1;
    }

    public UtilizatorF1 getUserByUsername(String username) {
        try {
            logger.info("Attempting to find user with username: {}", username);
            return utilizatorF1Repository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with username: {}", username);
                        return new RuntimeException("User not found with username: " + username);
                    });
        } catch (Exception e) {
            logger.error("Error finding user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find user: " + e.getMessage(), e);
        }
    }

    public List<FacturaF1> getFacturiByUsername(String username) {
        try {
            logger.info("Attempting to retrieve bills for username: {}", username);
            List<FacturaF1> bills = facturaF1Repository.findByUsername(username);
            logger.info("Successfully retrieved {} bills for username: {}", bills.size(), username);
            return bills;
        } catch (Exception e) {
            logger.error("Error retrieving bills for username {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve bills: " + e.getMessage(), e);
        }
    }

    public List<FacturaF1> getAllFacturi() {
        try {
            logger.info("Attempting to retrieve all facturi");
            List<FacturaF1> facturi = facturaF1Repository.findAll();
            logger.info("Successfully retrieved {} facturi", facturi.size());
            return facturi;
        } catch (Exception e) {
            logger.error("Error retrieving all facturi: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve facturi: " + e.getMessage(), e);
        }
    }

    public FacturaF1 getFacturaById(Long id) {
        try {
            logger.info("Attempting to retrieve factura with id: {}", id);
            return facturaF1Repository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Factura not found with id: {}", id);
                        return new RuntimeException("Factura not found with id: " + id);
                    });
        } catch (Exception e) {
            logger.error("Error retrieving factura with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve factura: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteFactura(Long id) {
        facturaF1Repository.deleteById(id);
        facturaRepository.deleteById(id);
    }

    @Transactional
    public FacturaF1 updateFactura(Long id, FacturaF1 facturaF1) {
        if (facturaF1Repository.existsById(id)) {
            facturaF1.setId(id);
            FacturaF1 updatedFacturaF1 = facturaF1Repository.save(facturaF1);

            Factura factura = facturaRepository.findById(id).orElse(new Factura());
            factura.setId(id);
            factura.setUsername(facturaF1.getUsername());
            factura.setSum(facturaF1.getSum());
            factura.setDatacr(facturaF1.getDatacr());
            factura.setDatasc(facturaF1.getDatasc());
            factura.setFurnizor(facturaF1.getFurnizor());
            factura.setPlatita(facturaF1.getPlatita());
            factura.setCode(facturaF1.getCode());
            factura.setAddress(facturaF1.getAddress());
            factura.setTip(facturaF1.getTip());
            factura.setPhone(facturaF1.getPhone());

            facturaRepository.save(factura);

            return updatedFacturaF1;
        }
        return null;
    }

    @Transactional
    public void payFactura(Long id, String username) {
        try {
            logger.info("Processing payment for factura {} by user {}", id, username);

            FacturaF1 factura = getFacturaById(id);

            UtilizatorF1 user = getUserByUsername(username);

            factura.setPlatita("DA");
            facturaF1Repository.save(factura);

            user.setBalance(user.getBalance() - factura.getSum());
            utilizatorF1Repository.save(user);

            logger.debug("Recording transaction for bill payment: ID={}, Username={}, Type={}, Amount={}", 
                id, username, factura.getTip(), factura.getSum());
            
            try {
                Tranzactie savedTranzactie = transactionService.recordBillPayment(
                    id,
                    username,
                    factura.getTip(),
                    factura.getSum()
                );
                logger.debug("Transaction recorded successfully: {}", savedTranzactie.getId());
            } catch (Exception e) {
                logger.error("Failed to record transaction: {}", e.getMessage());
            }
            
            logger.info("Successfully processed payment for factura {}", id);
        } catch (Exception e) {
            logger.error("Error processing payment for factura {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to process payment: " + e.getMessage(), e);
        }
    }
} 