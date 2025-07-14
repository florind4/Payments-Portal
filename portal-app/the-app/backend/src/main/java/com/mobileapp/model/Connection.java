package com.mobileapp.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "conexiuni")
public class Connection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String furnizor;

    @Column
    private String key;

    @Column
    private String portalname;

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

    public String getFurnizor() {
        return furnizor;
    }

    public void setFurnizor(String furnizor) {
        this.furnizor = furnizor;
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