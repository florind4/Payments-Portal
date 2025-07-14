package com.mobileapp.controller;

import com.mobileapp.dto.ConnectionRequest;
import com.mobileapp.model.ConexiuneF1;
import com.mobileapp.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/con")
@CrossOrigin(origins = "*")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/aprov")
    public ResponseEntity<?> createConnection(@RequestBody ConnectionRequest request) {
        try {
            ConexiuneF1 connection = connectionService.createConnection(request);
            return ResponseEntity.ok(connection.getKey());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 