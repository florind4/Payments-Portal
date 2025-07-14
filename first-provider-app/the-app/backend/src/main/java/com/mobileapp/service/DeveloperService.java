package com.mobileapp.service;

import com.mobileapp.dto.DeveloperDeleteRequest;
import com.mobileapp.dto.DeveloperLoginRequest;
import com.mobileapp.dto.DeveloperLoginResponse;
import com.mobileapp.dto.DeveloperProfileUpdateRequest;
import com.mobileapp.dto.DeveloperRegisterRequest;
import com.mobileapp.entity.Developer;
import com.mobileapp.repository.DeveloperRepository;
import com.mobileapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
public class DeveloperService {

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SECRET_LENGTH = 20;

    @Transactional
    public Developer register(DeveloperRegisterRequest request) {
        if (developerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Developer developer = new Developer();
        developer.setName(request.getName());
        developer.setEmail(request.getEmail());
        developer.setCompany(request.getCompany());
        developer.setJob(request.getJob());
        developer.setPassword(passwordEncoder.encode(request.getPassword()));
        developer.setPhone(request.getPhone());
        developer.setCountry(request.getCountry());
        developer.setAddress(request.getAddress());
        developer.setUrl(request.getUrl());
        developer.setSecret(generateSecret());

        return developerRepository.save(developer);
    }

    @Transactional
    public DeveloperLoginResponse login(DeveloperLoginRequest request) {
        Optional<Developer> developerOpt = developerRepository.findByEmail(request.getEmailOrName());
        if (!developerOpt.isPresent()) {
            developerOpt = developerRepository.findByName(request.getEmailOrName());
        }
        
        Developer developer = developerOpt.orElseThrow(() -> 
            new RuntimeException("Invalid email/name or password"));

        if (!passwordEncoder.matches(request.getPassword(), developer.getPassword())) {
            throw new RuntimeException("Invalid email/name or password");
        }

        String token = jwtTokenProvider.generateToken(developer.getEmail(), "DEVELOPER");
        return new DeveloperLoginResponse(token, developer.getSecret());
    }

    @Transactional(readOnly = true)
    public Developer getProfile(String token, String secret) {
        if (token == null || secret == null) {
            throw new RuntimeException("Missing authentication credentials");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String email = jwtTokenProvider.getEmailFromToken(token);
            if (email == null) {
                throw new RuntimeException("Invalid token");
            }

            Developer developer = developerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Developer not found"));

            if (!developer.getSecret().equals(secret)) {
                throw new RuntimeException("Invalid secret");
            }

            return developer;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get profile: " + e.getMessage());
        }
    }

    @Transactional
    public Developer updateProfile(String token, String secret, DeveloperProfileUpdateRequest request) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String email = jwtTokenProvider.getEmailFromToken(token);
            Developer developer = developerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Developer not found"));

            if (!developer.getSecret().equals(secret)) {
                throw new RuntimeException("Invalid secret");
            }

            developer.setName(request.getName());
            developer.setCompany(request.getCompany());
            developer.setJob(request.getJob());
            developer.setPhone(request.getPhone());
            developer.setCountry(request.getCountry());
            developer.setAddress(request.getAddress());
            developer.setUrl(request.getUrl());

            return developerRepository.save(developer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAccount(String token, String secret, String password) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String email = jwtTokenProvider.getEmailFromToken(token);
            Developer developer = developerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Developer not found"));

            if (!developer.getSecret().equals(secret)) {
                throw new RuntimeException("Invalid secret");
            }

            if (!passwordEncoder.matches(password, developer.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            developerRepository.delete(developer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete account: " + e.getMessage());
        }
    }

    private String generateSecret() {
        Random random = new Random();
        StringBuilder secret = new StringBuilder(SECRET_LENGTH);
        for (int i = 0; i < SECRET_LENGTH; i++) {
            secret.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return secret.toString();
    }
} 