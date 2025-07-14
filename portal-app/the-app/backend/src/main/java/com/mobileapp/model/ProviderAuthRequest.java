package com.mobileapp.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ProviderAuthRequest {
    private String username;
    private String password;
    private String secret;
    @JsonProperty("portalUsername")
    private String portalUsername;
} 