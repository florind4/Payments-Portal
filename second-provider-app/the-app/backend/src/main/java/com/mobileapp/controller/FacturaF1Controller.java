package com.mobileapp.controller;

import com.mobileapp.entity.FacturaF1;
import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.service.FacturaF1Service;
import com.mobileapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mobileapp.payload.response.MessageResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/facturi")
@CrossOrigin(origins = "*")
public class FacturaF1Controller {
    private static final Logger logger = LoggerFactory.getLogger(FacturaF1Controller.class);

    @Autowired
    private FacturaF1Service facturaF1Service;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createFactura(@RequestBody FacturaF1 factura) {
        try {
            logger.info("Received request to create factura: {}", factura);

            UtilizatorF1 user = facturaF1Service.getUserByUsername(factura.getUsername());
            logger.info("Found user: {}", user);
            
            if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
                logger.error("User {} is trying to create a bill but has no address.", user.getUsername());
                return ResponseEntity.badRequest().body(new MessageResponse("Please update your profile with an address before creating a bill."));
            }

            factura.setPhone(user.getPhone());
            factura.setAddress(user.getAddress());
            factura.setCode(user.getCode());

            factura.setDatacr(LocalDate.now());
            factura.setPlatita("NU");

            logger.info("Saving factura with details: username={}, phone={}, address={}, code={}, sum={}, type={}, dueDate={}",
                factura.getUsername(),
                factura.getPhone(),
                factura.getAddress(),
                factura.getCode(),
                factura.getSum(),
                factura.getTip(),
                factura.getDatasc()
            );
            
            FacturaF1 savedFactura = facturaF1Service.saveFactura(factura);
            logger.info("Successfully created factura with id: {}", savedFactura.getId());
            return ResponseEntity.ok(savedFactura);
        } catch (Exception e) {
            logger.error("Error creating factura: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error creating factura: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getFacturi(@RequestParam(required = false) String username) {
        try {
            if (username != null && !username.isEmpty()) {
                logger.info("Received request to get facturi for username: {}", username);
                List<FacturaF1> facturi = facturaF1Service.getFacturiByUsername(username);
                logger.info("Successfully retrieved {} facturi for username: {}", facturi.size(), username);
                return ResponseEntity.ok(facturi);
            } else {
                logger.info("Received request to get all facturi");
                List<FacturaF1> facturi = facturaF1Service.getAllFacturi();
                logger.info("Successfully retrieved {} facturi", facturi.size());
                return ResponseEntity.ok(facturi);
            }
        } catch (Exception e) {
            logger.error("Error retrieving facturi: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving facturi: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacturaById(@PathVariable Long id) {
        try {
            logger.info("Received request to get factura with id: {}", id);
            FacturaF1 factura = facturaF1Service.getFacturaById(id);
            logger.info("Successfully retrieved factura with id: {}", id);
            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            logger.error("Error retrieving factura with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving factura: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payFactura(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            logger.info("Received request to pay factura with id: {}", id);
            String username = userService.getUsernameFromToken(token);

            FacturaF1 factura = facturaF1Service.getFacturaById(id);

            if (!factura.getUsername().equals(username)) {
                logger.error("User {} is not authorized to pay factura {}", username, id);
                return ResponseEntity.status(403).body("Not authorized to pay this bill");
            }

            if ("DA".equals(factura.getPlatita())) {
                logger.error("Factura {} is already paid", id);
                return ResponseEntity.badRequest().body("Bill is already paid");
            }

            Double userBalance = userService.getUserBalance(username);

            if (userBalance < factura.getSum()) {
                logger.error("User {} has insufficient balance to pay factura {}", username, id);
                return ResponseEntity.badRequest().body("Insufficient balance");
            }

            facturaF1Service.payFactura(id, username);
            logger.info("Successfully paid factura with id: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error paying factura with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error paying bill: " + e.getMessage());
        }
    }
} 