package com.mobileapp.service;

import com.mobileapp.model.Provider;
import com.mobileapp.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Provider getProviderById(Long id) {
        return providerRepository.findById(id).orElse(null);
    }

    public Provider getProviderByName(String name) {
        return providerRepository.findByName(name);
    }
} 