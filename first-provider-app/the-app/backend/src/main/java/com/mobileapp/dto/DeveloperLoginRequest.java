package com.mobileapp.dto;

import lombok.Data;

@Data
public class DeveloperLoginRequest {
    private String emailOrName;
    private String password;
} 