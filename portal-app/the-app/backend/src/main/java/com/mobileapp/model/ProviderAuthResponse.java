package com.mobileapp.model;

import lombok.Data;

@Data
public class ProviderAuthResponse {
    private boolean success;
    private String message;
    private String key;
    private String providerName;
} 