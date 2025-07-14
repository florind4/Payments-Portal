package com.mobileapp.service;

import com.mobileapp.model.Connection;
import com.mobileapp.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    public String generateKey() {
        return UUID.randomUUID().toString();
    }

    public Connection saveConnection(Connection connection) {
        return connectionRepository.save(connection);
    }

    public List<Connection> getUserConnections(String username) {
        return connectionRepository.findByUsername(username);
    }

    public Connection getConnection(String username, String furnizor) {
        return connectionRepository.findByUsernameAndFurnizor(username, furnizor);
    }
} 