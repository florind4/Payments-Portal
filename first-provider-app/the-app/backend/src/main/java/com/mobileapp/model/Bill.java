package com.mobileapp.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturif1")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "tip")
    private String tip;

    @Column(name = "sum")
    private Integer sum;

    @Column(name = "datasc")
    private LocalDate datasc;

    @Column(name = "datacr")
    private LocalDate datacr;

    @Column(name = "furnizor")
    private String furnizor;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "code")
    private Integer code;

    @Column(name = "platita")
    private String platita;

    @Column(name = "status")
    private String status;

    @Column(name = "is_scheduled")
    private Boolean isScheduled;

    @Column(name = "scheduled_datetime")
    private LocalDateTime scheduledDateTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDate getDatacr() {
        return datacr;
    }

    public void setDatacr(LocalDate datacr) {
        this.datacr = datacr;
    }

    public String getFurnizor() {
        return furnizor;
    }

    public void setFurnizor(String furnizor) {
        this.furnizor = furnizor;
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPlatita() {
        return platita;
    }

    public void setPlatita(String platita) {
        this.platita = platita;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }
} 