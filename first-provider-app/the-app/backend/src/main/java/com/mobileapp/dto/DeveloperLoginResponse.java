package com.mobileapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeveloperLoginResponse {
    private String token;
    private String secret;
} 