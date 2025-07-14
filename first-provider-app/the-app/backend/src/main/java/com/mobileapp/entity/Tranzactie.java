package com.mobileapp.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tranzactiif1")
public class Tranzactie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_factura", nullable = false)
    private Long idFactura;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "tip", nullable = false, length = 255)
    private String tip;

    @Column(name = "sum", nullable = false)
    private Double sum;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public Tranzactie() {
    }

    public Tranzactie(Long idFactura, String username, String tip, Double sum, LocalDate date) {
        this.idFactura = idFactura;
        this.username = username;
        this.tip = tip;
        this.sum = sum;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
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

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Tranzactie{" +
                "id=" + id +
                ", idFactura=" + idFactura +
                ", username='" + username + '\'' +
                ", tip='" + tip + '\'' +
                ", sum=" + sum +
                ", date=" + date +
                '}';
    }
} 