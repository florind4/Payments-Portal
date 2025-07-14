package com.mobileapp.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "utilizatorif1")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "photo", columnDefinition = "TEXT")
    private String photo;

    @Column(name = "country")
    private String country;

    @Column(name = "code", unique = true)
    private Integer code;

    @Column(name = "address")
    private String address;

    @Column(name = "postal_code", length = 8)
    private String postalCode;

    @Column(name = "currency", nullable = false, columnDefinition = "VARCHAR(3) DEFAULT 'RON'")
    private String currency = "RON";

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
} 