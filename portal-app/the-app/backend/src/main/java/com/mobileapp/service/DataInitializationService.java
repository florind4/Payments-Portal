package com.mobileapp.service;

import com.mobileapp.model.Provider;
import com.mobileapp.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeProviders();
    }

    private void initializeProviders() {
        logger.info("Initializing providers data...");

        if (providerRepository.findByName("F-Telecom") == null) {
            Provider fTelecom = new Provider();
            fTelecom.setName("F-Telecom");
            fTelecom.setUrl("http://localhost:8100");
            fTelecom.setSecret("f1_secret_key_2024");
            fTelecom.setDescription("Servicii de telecomunicații - internet, telefonie și televiziune");
            providerRepository.save(fTelecom);
            logger.info("Created F-Telecom provider");
        } else {
            logger.info("F-Telecom provider already exists");
        }

        if (providerRepository.findByName("F-Electrica") == null) {
            Provider fElectrica = new Provider();
            fElectrica.setName("F-Electrica");
            fElectrica.setUrl("http://localhost:8101");
            fElectrica.setSecret("f2_secret_key_2024");
            fElectrica.setDescription("Servicii de energie electrică și utilități");
            providerRepository.save(fElectrica);
            logger.info("Created F-Electrica provider");
        } else {
            logger.info("F-Electrica provider already exists");
        }
        
        logger.info("Providers initialization completed");
    }
} 