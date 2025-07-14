package com.mobileapp.security;

import com.mobileapp.entity.Developer;
import com.mobileapp.repository.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeveloperUserDetailsService implements UserDetailsService {

    @Autowired
    private DeveloperRepository developerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Developer developer = developerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Developer not found with email: " + email));

        return DeveloperPrincipal.create(developer);
    }
} 