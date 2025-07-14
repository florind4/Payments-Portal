package com.mobileapp.security;

import com.mobileapp.model.User;
import com.mobileapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username or email: {}", usernameOrEmail);
        
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
            .orElseThrow(() -> {
                logger.error("User not found with username or email: {}", usernameOrEmail);
                return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
            });

        logger.info("Found user: {}", user.getUsername());
        logger.debug("User password hash: {}", user.getPassword());
        logger.debug("User email: {}", user.getEmail());
        logger.debug("User ID: {}", user.getId());

        UserDetails userDetails = UserPrincipal.create(user);
        logger.debug("Created UserDetails with authorities: {}", userDetails.getAuthorities());
        
        return userDetails;
    }
} 