package com.mobileapp.dto;

import lombok.Data;

@Data
public class DeveloperRegisterRequest {
    private String name;
    private String email;
    private String company;
    private String job;
    private String password;
    private String phone;
    private String country;
    private String address;
    private String url;
} 