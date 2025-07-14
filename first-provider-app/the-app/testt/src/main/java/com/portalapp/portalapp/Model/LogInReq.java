package com.portalapp.portalapp.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-args constructor
@AllArgsConstructor // Generates an all-args constructor
public class LogInReq {


    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String birthday;
    private String phone;
    private String password;
}
