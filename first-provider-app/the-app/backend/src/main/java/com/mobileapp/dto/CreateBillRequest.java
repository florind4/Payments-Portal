package com.mobileapp.dto;

import java.time.LocalDate;

public class CreateBillRequest {
    private String username;
    private String tip;
    private String type;
    private Integer sum;
    private LocalDate datasc;
    private String phone;
    private String address;
    private String code;
    private Long scheduledDateTime;
    private Boolean isScheduled;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public LocalDate getDatasc() {
        return datasc;
    }

    public void setDatasc(LocalDate datasc) {
        this.datasc = datasc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(Long scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }
} 