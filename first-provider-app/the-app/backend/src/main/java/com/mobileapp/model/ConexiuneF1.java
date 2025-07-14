package com.mobileapp.model;

import javax.persistence.*;

@Entity
@Table(name = "conexiunif1")
public class ConexiuneF1 {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String dev;

    @Column
    private String key;

    @Column(name = "portalname")
    private String portalname;

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

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPortalname() {
        return portalname;
    }

    public void setPortalname(String portalname) {
        this.portalname = portalname;
    }
} 