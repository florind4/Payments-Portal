package com.mobileapp.service;

import com.mobileapp.dto.ConnectionRequest;
import com.mobileapp.model.ConexiuneF1;
import com.mobileapp.entity.Developer;
import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.repository.ConexiuneF1Repository;
import com.mobileapp.repository.DeveloperRepository;
import com.mobileapp.repository.UtilizatorF1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
public class ConnectionService {

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private ConexiuneF1Repository conexiuneF1Repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int KEY_LENGTH = 25;

    @Transactional
    public ConexiuneF1 createConnection(ConnectionRequest request) {

        Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        UtilizatorF1 user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        Optional<Developer> developerOpt = developerRepository.findBySecret(request.getSecret());
        if (developerOpt.isEmpty()) {
            throw new RuntimeException("Invalid developer secret");
        }

        Developer developer = developerOpt.get();

        if (conexiuneF1Repository.existsByUsernameAndDev(user.getUsername(), developer.getCompany())) {
            throw new RuntimeException("Connection already exists");
        }

        String key = generateRandomKey();

        ConexiuneF1 connection = new ConexiuneF1();
        connection.setUsername(user.getUsername());
        connection.setDev(developer.getCompany());
        connection.setKey(key);

        return conexiuneF1Repository.save(connection);
    }

    private String generateRandomKey() {
        Random random = new Random();
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            key.append(CHARACTERS.charAt(index));
        }
        return key.toString();
    }
} 