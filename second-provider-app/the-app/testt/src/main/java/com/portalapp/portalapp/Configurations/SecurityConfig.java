package com.portalapp.portalapp.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/users/conprov").permitAll()
                .requestMatchers("/api/users/conprov1").permitAll()
                .requestMatchers("/api/users/login").permitAll()
                .requestMatchers("/api/factura/{billId}").permitAll()
                .requestMatchers("/api/factura/download/{billId}").permitAll()
                .requestMatchers("/api/users/receivebill").permitAll()
                .requestMatchers("/api/appreg/process").permitAll()
                .requestMatchers("/api/users/conprov").permitAll()
                .anyRequest().authenticated() 
            );
        http.headers().frameOptions().sameOrigin();
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}