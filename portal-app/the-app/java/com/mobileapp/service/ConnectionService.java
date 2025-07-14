package com.mobileapp.service;

import com.mobileapp.model.Connection;
import com.mobileapp.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConnectionService {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionService.class);

    @Autowired
    private ConnectionRepository connectionRepository;

    @Transactional
    public Connection saveConnection(String username, String furnizor, String key) {
        try {
            logger.info("Attempting to save connection for user: {}, provider: {}", username, furnizor);
            
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            if (furnizor == null || furnizor.trim().isEmpty()) {
                throw new IllegalArgumentException("Provider name cannot be null or empty");
            }
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("Key cannot be null or empty");
            }

            // Check if connection already exists
            Connection existingConnection = connectionRepository.findByUsernameAndFurnizor(username, furnizor);
            
            if (existingConnection != null) {
                logger.info("Updating existing connection for user: {}, provider: {}", username, furnizor);
                // Update existing connection
                existingConnection.setKey(key);
                Connection updatedConnection = connectionRepository.save(existingConnection);
                logger.info("Successfully updated connection");
                return updatedConnection;
            } else {
                logger.info("Creating new connection for user: {}, provider: {}", username, furnizor);
                // Create new connection
                Connection newConnection = new Connection();
                newConnection.setUsername(username);
                newConnection.setFurnizor(furnizor);
                newConnection.setKey(key);
                Connection savedConnection = connectionRepository.save(newConnection);
                logger.info("Successfully created new connection");
                return savedConnection;
            }
        } catch (Exception e) {
            logger.error("Error saving connection: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save connection: " + e.getMessage(), e);
        }
    }

    public List<Connection> getUserConnections(String username) {
        try {
            logger.info("Fetching connections for user: {}", username);
            List<Connection> connections = connectionRepository.findByUsername(username);
            logger.info("Found {} connections for user: {}", connections.size(), username);
            return connections;
        } catch (Exception e) {
            logger.error("Error fetching user connections: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch user connections: " + e.getMessage(), e);
        }
    }

    public Connection getConnection(String username, String furnizor) {
        try {
            logger.info("Fetching connection for user: {}, provider: {}", username, furnizor);
            Connection connection = connectionRepository.findByUsernameAndFurnizor(username, furnizor);
            if (connection != null) {
                logger.info("Found connection");
            } else {
                logger.info("No connection found");
            }
            return connection;
        } catch (Exception e) {
            logger.error("Error fetching connection: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch connection: " + e.getMessage(), e);
        }
    }
} 