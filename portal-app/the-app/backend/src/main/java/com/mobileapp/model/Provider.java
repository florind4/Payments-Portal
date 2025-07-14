package com.mobileapp.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "furnizori")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String secret;

    @Column
    private String description;
} 