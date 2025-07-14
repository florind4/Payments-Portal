package com.mobileapp.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "facturi")
public class Bill {
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

    @Column(name = "furnizor")
    private String furnizor;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "code")
    private Integer code;

    @Column(name = "tip")
    private String tip;

    @Column(name = "platita")
    private String platita;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "notification_sent")
    private Boolean notificationSent = false;
} 