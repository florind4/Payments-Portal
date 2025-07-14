package com.mobileapp.dto;

import lombok.Data;

@Data
public class DeveloperProfileUpdateRequest {
    private String name;
    private String company;
    private String job;
    private String phone;
    private String country;
    private String address;
    private String url;
} 