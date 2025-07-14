package com.mobileapp.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "developers")
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String job;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    private String country;

    private String address;

    private String url;

    @Column(nullable = false, unique = true)
    private String secret;
} 