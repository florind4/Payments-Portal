package com.mobileapp.service;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.UtilizatorF1Repository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
            logger.info("Processing token: {}", token);
            
            if (token == null || token.trim().isEmpty()) {
                logger.error("Token is null or empty");
                throw new RuntimeException("Invalid token");
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
                logger.info("Token after removing Bearer prefix: {}", token);
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            logger.info("Successfully extracted username from token: {}", username);

            Optional<UtilizatorF1> user = utilizatorF1Repository.findByUsername(username);
            if (!user.isPresent()) {
                logger.error("User not found in database for username: {}", username);
                throw new RuntimeException("User not found");
            }
            
            logger.info("Found user in database: {}", user.get().getUsername());
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    public Double getUserBalance(String username) {
        logger.info("Getting balance for user: {}", username);
        try {
            Optional<UtilizatorF1> user = utilizatorF1Repository.findByUsername(username);
            if (user.isPresent()) {
                Double balance = user.get().getBalance();
                logger.info("Current balance for user {}: {}", username, balance);
                return balance != null ? balance : 0.0;
            }
            logger.error("User not found when getting balance: {}", username);
            throw new RuntimeException("User not found");
        } catch (Exception e) {
            logger.error("Error getting user balance: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting user balance: " + e.getMessage());
        }
    }

    public boolean isUserAdmin(String username) {
        try {
            logger.info("Checking admin status for user: {}", username);
            Optional<UtilizatorF1> user = utilizatorF1Repository.findByUsername(username);
            if (user.isPresent()) {
                boolean isAdmin = "DA".equals(user.get().getAdmin());
                logger.info("User {} admin status: {}", username, isAdmin);
                return isAdmin;
            }
            logger.error("User not found when checking admin status: {}", username);
            return false;
        } catch (Exception e) {
            logger.error("Error checking admin status: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<UtilizatorF1> getAllUsers() {
        try {
            logger.info("Getting all users");
            List<UtilizatorF1> users = utilizatorF1Repository.findAll();
            logger.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting users: " + e.getMessage());
        }
    }

    @Transactional
    public void updateUserAdminStatus(String username, Boolean isAdmin) {
        try {
            logger.info("Updating admin status for user {} to {}", username, isAdmin);
            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            if (userOpt.isPresent()) {
                UtilizatorF1 user = userOpt.get();
                user.setAdmin(isAdmin ? "DA" : "NU");
                utilizatorF1Repository.save(user);
                logger.info("Successfully updated admin status for user {}", username);
            } else {
                logger.error("User not found when updating admin status: {}", username);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error updating admin status: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating admin status: " + e.getMessage());
        }
    }

    @Transactional
    public void updateUserDetails(String username, Map<String, String> updates) {
        try {
            logger.info("Updating details for user {}: {}", username, updates);
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
                logger.info("Successfully updated details for user {}", username);
            } else {
                logger.error("User not found when updating details: {}", username);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error updating user details: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating user details: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteUser(String username) {
        try {
            logger.info("Deleting user: {}", username);
            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(username);
            if (userOpt.isPresent()) {
                utilizatorF1Repository.delete(userOpt.get());
                logger.info("Successfully deleted user {}", username);
            } else {
                logger.error("User not found when deleting: {}", username);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }
} 