package com.mobileapp.controller;

import com.mobileapp.entity.UtilizatorF1;
import com.mobileapp.payload.LoginRequest;
import com.mobileapp.payload.SignUpRequest;
import com.mobileapp.payload.response.MessageResponse;
import com.mobileapp.repository.UtilizatorF1Repository;
import com.mobileapp.security.JwtTokenProvider;
import com.mobileapp.entity.Developer;
import com.mobileapp.model.ConexiuneF1;
import com.mobileapp.repository.DeveloperRepository;
import com.mobileapp.repository.ConexiuneF1Repository;
import com.mobileapp.dto.ProviderAuthRequest;
import com.mobileapp.service.ConexiuniF1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilizatorF1Repository utilizatorF1Repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private ConexiuneF1Repository conexiuneF1Repository;

    @Autowired
    private ConexiuniF1Service conexiuniF1Service;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("[AuthController] Attempting authentication for: " + loginRequest.getUsernameOrEmail());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
            )
        );
        System.out.println("[AuthController] Authentication successful for: " + loginRequest.getUsernameOrEmail());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("username", loginRequest.getUsernameOrEmail());

        return ResponseEntity.ok(response);
    }

    private Integer generateUniqueCode() {
        Random random = new Random();
        Integer code;
        int maxAttempts = 100;
        int attempts = 0;
        
        do {
            code = random.nextInt(98999) + 1001;
            attempts++;
            if (attempts >= maxAttempts) {
                logger.error("Failed to generate unique code after {} attempts", maxAttempts);
                throw new RuntimeException("Unable to generate unique user code");
            }
        } while (utilizatorF1Repository.findByCode(code).isPresent());
        
        logger.debug("Generated unique code: {} after {} attempts", code, attempts);
        return code;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            logger.info("Received registration request for username: {}", signUpRequest.getUsername());

            if (signUpRequest.getUsername() == null || signUpRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is required!"));
            }
            
            if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is required!"));
            }
            
            if (signUpRequest.getPassword() == null || signUpRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is required!"));
            }
            
            if (utilizatorF1Repository.findByUsername(signUpRequest.getUsername()).isPresent()) {
                logger.warn("Registration failed: Username {} is already taken", signUpRequest.getUsername());
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
            }

            if (utilizatorF1Repository.findByEmail(signUpRequest.getEmail()).isPresent()) {
                logger.warn("Registration failed: Email {} is already in use", signUpRequest.getEmail());
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            }

            UtilizatorF1 user = new UtilizatorF1();
            user.setUsername(signUpRequest.getUsername().trim());
            user.setEmail(signUpRequest.getEmail().trim());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setPhone(signUpRequest.getPhoneNumber());
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());

            if (signUpRequest.getBirthday() != null && !signUpRequest.getBirthday().isEmpty()) {
                try {
                    LocalDate birthday = LocalDate.parse(signUpRequest.getBirthday());
                    user.setBirthday(birthday);
                } catch (Exception e) {
                    logger.error("Invalid birthday format: {}", signUpRequest.getBirthday());
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid birthday format! Use YYYY-MM-DD"));
                }
            } else {
                user.setBirthday(LocalDate.of(1990, 1, 1));
            }
            
            user.setCode(generateUniqueCode());
            user.setBalance(0);
            user.setAdmin("NU");
            user.setCurrency("RON");
            user.setNrBills(0);

            logger.info("Saving new user with username: {}", user.getUsername());
            utilizatorF1Repository.save(user);
            logger.info("Successfully registered user: {}", user.getUsername());

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new MessageResponse("Error: Registration failed. Please try again."));
        }
    }

    @PostMapping("/portal-auth")
    public ResponseEntity<?> authenticatePortalUser(@RequestBody ProviderAuthRequest request, HttpServletRequest httpRequest) {
        logger.info("Received portal authentication request");

        String portalUsername = httpRequest.getHeader("X-Portal-Username");
        logger.info("Portal username from header: {}", portalUsername);
        
        if (portalUsername == null || portalUsername.isEmpty()) {
            logger.error("No portal username provided in header");
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Portal username is required"
            ));
        }
        
        logger.info("Request details:");
        logger.info("- Provider Username: {}", request.getUsername());
        logger.info("- Portal Username: {}", portalUsername);
        logger.info("- Secret: {}", request.getSecret());
        logger.debug("Full request body: {}", request);

        try {
             Optional<Developer> developerOpt = developerRepository.findBySecret(request.getSecret());
            if (developerOpt.isEmpty()) {
                logger.error("Invalid developer secret");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid developer secret"
                ));
            }

            Developer developer = developerOpt.get();
            logger.info("Found developer: {}", developer.getName());

            Optional<UtilizatorF1> userOpt = utilizatorF1Repository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                logger.error("User not found: {}", request.getUsername());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid username or password"
                ));
            }

            UtilizatorF1 user = userOpt.get();
            logger.info("Found user in database: {}", user.getUsername());
            logger.info("Stored password hash: {}", user.getPassword());
            logger.info("Received password: {}", request.getPassword());

            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            logger.info("Password matches: {}", passwordMatches);

            if (!passwordMatches) {
                logger.error("Invalid password for user: {}", request.getUsername());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid username or password"
                ));
            }

            logger.info("User authenticated successfully: {}", request.getUsername());

            try {
                String key = conexiuniF1Service.saveConexiune(
                    request.getUsername(),
                    developer.getName(),
                    portalUsername
                );

                logger.info("Created new connection with details:");
                logger.info("- Provider Username: {}", request.getUsername());
                logger.info("- Provider Name: {}", developer.getName());
                logger.info("- Portal Username: {}", portalUsername);
                logger.info("- Generated Key: {}", key);

                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully authenticated",
                    "key", key,
                    "providerName", developer.getName()
                ));
            } catch (Exception e) {
                logger.error("Error saving connection: {}", e.getMessage(), e);
                throw e;
            }

        } catch (Exception e) {
            logger.error("Error during portal authentication: {}", e.getMessage(), e);
            logger.error("Stack trace:", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "An error occurred during authentication: " + e.getMessage()
            ));
        }
    }

    private String generateRandomKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder key = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            key.append(chars.charAt(random.nextInt(chars.length())));
        }
        return key.toString();
    }
} 