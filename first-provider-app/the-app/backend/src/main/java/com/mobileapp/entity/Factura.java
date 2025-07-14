package com.mobileapp.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "facturi")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Double sum;

    @Column(nullable = false)
    private LocalDate datacr;

    @Column(nullable = false)
    private LocalDate datasc;

    @Column(nullable = false)
    private String furnizor;

    @Column(nullable = false)
    private String platita = "NU";

    @Column(nullable = false)
    private Integer code;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String tip;

    @Column(nullable = false)
    private String phone;

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

    public String getFurnizor() {
        return furnizor;
    }

    public void setFurnizor(String furnizor) {
        this.furnizor = furnizor;
    }

    public String getPlatita() {
        return platita;
    }

    public void setPlatita(String platita) {
        this.platita = platita;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
} 