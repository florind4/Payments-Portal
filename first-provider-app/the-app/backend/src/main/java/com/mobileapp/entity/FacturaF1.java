package com.mobileapp.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "facturif1")
public class FacturaF1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "sum")
    private Double sum;

    @Column(name = "datacr")
    private LocalDate datacr;

    @Column(name = "datasc")
    private LocalDate datasc;

    @Column(name = "platita")
    private String platita;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "code")
    private Integer code;

    @Column(name = "tip")
    private String tip;

    @Column(name = "furnizor")
    private String furnizor;

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

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public LocalDate getDatacr() {
        return datacr;
    }

    public void setDatacr(LocalDate datacr) {
        this.datacr = datacr;
    }

    public LocalDate getDatasc() {
        return datasc;
    }

    public void setDatasc(LocalDate datasc) {
        this.datasc = datasc;
    }

    public String getPlatita() {
        return platita;
    }

    public void setPlatita(String platita) {
        this.platita = platita;
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

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getFurnizor() {
        return furnizor;
    }

    public void setFurnizor(String furnizor) {
        this.furnizor = furnizor;
    }
} 