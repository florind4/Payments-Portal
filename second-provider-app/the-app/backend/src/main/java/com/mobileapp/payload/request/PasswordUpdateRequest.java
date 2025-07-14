package com.mobileapp.payload.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordUpdateRequest {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;
} 