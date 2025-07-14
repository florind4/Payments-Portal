package com.portalapp.portalapp.Model;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode
@AllArgsConstructor // Generates an all-args constructor
public class LogInResp {
    private final String jwt;
}
