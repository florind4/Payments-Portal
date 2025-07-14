package com.mobileapp.dto;

import lombok.Data;

@Data
public class ConnectionRequest {
    private String username;
    private String password;
    private String secret;
} 