package com.mobileapp.controller;

import com.mobileapp.dto.ProviderAuthRequest;
import com.mobileapp.service.ConexiuniF1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProviderAuthController {

    @Autowired
    private ConexiuniF1Service conexiuniF1Service;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticateProvider(@RequestBody ProviderAuthRequest request) {
        try {
            String key = conexiuniF1Service.saveConexiune(
                request.getUsername(),
                request.getSecret(),
                request.getPortalUsername()
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully authenticated",
                "key", key
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
} 