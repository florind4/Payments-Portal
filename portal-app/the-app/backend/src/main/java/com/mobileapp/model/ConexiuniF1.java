package com.mobileapp.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "conexiunif1")
public class ConexiuniF1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "key")
    private String key;

    @Column(name = "portalname")
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