package com.mobileapp.dto;

import lombok.Data;

@Data
public class ProviderAuthRequest {
    private String username;
    private String password;
    private String secret;
    private String portalUsername;
} 