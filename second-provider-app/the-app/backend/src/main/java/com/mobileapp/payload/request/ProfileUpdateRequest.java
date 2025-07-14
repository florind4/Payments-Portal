package com.mobileapp.payload.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class ProfileUpdateRequest {
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 20)
    private String phoneNumber;

    private String birthday;

    private String photo;

    private String country;

    private String address;

    @Size(max = 8)
    private String postalCode;
} 