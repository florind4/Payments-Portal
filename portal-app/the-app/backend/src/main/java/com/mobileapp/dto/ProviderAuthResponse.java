package com.mobileapp.dto;

import lombok.Data;

@Data
public class ProviderAuthResponse {
    private boolean success;
    private String message;
    private String key;
} 