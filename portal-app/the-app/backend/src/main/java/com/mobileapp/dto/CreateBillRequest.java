package com.mobileapp.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateBillRequest {
    private String username;
    private String type;
    private Integer sum;
    private LocalDate datasc;
} 