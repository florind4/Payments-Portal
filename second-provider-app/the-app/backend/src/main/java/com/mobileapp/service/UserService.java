package com.mobileapp.service;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.UtilizatorF1Repository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getUsernameFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            logger.info("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token");
        }
    }

    public Double getUserBalance(String username) {
        try {
            logger.info("Getting balance for user: {}", username);
        Optional<UtilizatorF1> user = utilizatorF1Repository.findByUsername(username);
        if (user.isPresent()) {
                Integer balance = user.get().getBalance();
                logger.info("Current balance for user {}: {}", username, balance);
                return balance != null ? balance.doubleValue() : 0.0;
            }
            logger.error("User not found when getting balance: {}", username);
            throw new RuntimeException("User not found");
        } catch (Exception e) {
            logger.error("Error getting user balance: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting user balance: " + e.getMessage());
        }
    }

    public boolean isUserAdmin(String username) {
        Optional<UtilizatorF1> user = utilizatorF1Repository.findByUsername(username);
        if (user.isPresent()) {
            return "DA".equals(user.get().getAdmin());
        }
        return false;
    }

    public List<UtilizatorF1> getAllUsers() {
        return utilizatorF1Repository.findAll();
    }

    @Transactional
    public void updateUserAdminStatus(String username, Boolean isAdmin) {
        Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
        if (userOpt.isPresent()) {
            UtilizatorF1 user = userOpt.get();
            user.setAdmin(isAdmin ? "DA" : "NU");
            utilizatorF1Repository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void updateUserDetails(String username, Map<String, String> updates) {
        Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
        if (userOpt.isPresent()) {
            UtilizatorF1 user = userOpt.get();
            if (updates.containsKey("username")) {
                user.setUsername(updates.get("username"));
            }
            if (updates.containsKey("phone")) {
                user.setPhone(updates.get("phone"));
            }
            if (updates.containsKey("email")) {
                user.setEmail(updates.get("email"));
            }
            utilizatorF1Repository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void deleteUser(String username) {
        Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
        if (userOpt.isPresent()) {
            utilizatorF1Repository.delete(userOpt.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void addFunds(String username, Double amount) {
        Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
        if (userOpt.isPresent()) {
            UtilizatorF1 user = userOpt.get();
            Integer currentBalance = user.getBalance() != null ? user.getBalance() : 0;
            Integer newBalance = currentBalance + amount.intValue();
            user.setBalance(newBalance);
            utilizatorF1Repository.save(user);
            logger.info("Added {} funds to user {}. New balance: {}", amount, username, newBalance);
        } else {
            logger.error("User not found: {}", username);
            throw new RuntimeException("User not found");
        }
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            logger.error("Error verifying password: {}", e.getMessage(), e);
            return false;
        }
    }

    public String encodePassword(String rawPassword) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            logger.error("Error encoding password: {}", e.getMessage(), e);
            throw new RuntimeException("Error encoding password");
        }
    }
} 