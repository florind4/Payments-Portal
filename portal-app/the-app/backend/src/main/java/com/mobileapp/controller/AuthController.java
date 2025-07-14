package com.mobileapp.controller;

import com.mobileapp.model.User;
import com.mobileapp.payload.LoginRequest;
import com.mobileapp.payload.SignUpRequest;
import com.mobileapp.payload.response.MessageResponse;
import com.mobileapp.repository.UserRepository;
import com.mobileapp.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());

        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail()).orElse(null);
        if (user == null) {
            logger.error("User not found: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.status(403).body(new MessageResponse("Invalid username or password"));
        }
        
        logger.info("Found user in database: {}", user.getUsername());
        logger.debug("Stored password hash: {}", user.getPassword());
        logger.debug("Attempting to authenticate with password length: {}", loginRequest.getPassword().length());
        
        try {
            logger.debug("Attempting authentication with username: {}", loginRequest.getUsernameOrEmail());
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );

            logger.info("Authentication successful for user: {}", loginRequest.getUsernameOrEmail());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", loginRequest.getUsernameOrEmail());

            logger.info("Login successful for user: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", loginRequest.getUsernameOrEmail());
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(403).body(new MessageResponse("Invalid username or password"));
        } catch (Exception e) {
            logger.error("Login error for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ResponseEntity.status(500).body(new MessageResponse("An error occurred during login"));
        }
    }

    private Integer generateUniqueCode() {
        Random random = new Random();
        Integer code;
        do {
            code = random.nextInt(98999) + 1001;
        } while (userRepository.existsByCode(code));
        return code;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.info("Received registration request for username: {}", signUpRequest.getUsername());
        logger.debug("Registration data: {}", signUpRequest);
        
        try {
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                logger.warn("Registration failed: Username {} is already taken", signUpRequest.getUsername());
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
            }

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                logger.warn("Registration failed: Email {} is already in use", signUpRequest.getEmail());
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            }

            logger.debug("Creating new user with username: {}", signUpRequest.getUsername());
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            
            String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
            logger.debug("Password encoded successfully");
            user.setPassword(encodedPassword);
            
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setCode(generateUniqueCode());
            if (signUpRequest.getBirthday() != null) {
                user.setBirthday(LocalDate.parse(signUpRequest.getBirthday()));
            }
            
            logger.info("Saving new user: {}", user.getUsername());
            userRepository.save(user);
            logger.info("User registered successfully: {}", user.getUsername());
            
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (Exception e) {
            logger.error("Error during user registration", e);
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
} 