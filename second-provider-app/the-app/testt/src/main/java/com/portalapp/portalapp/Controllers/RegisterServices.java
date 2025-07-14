package com.portalapp.portalapp.Controllers;

import com.portalapp.portalapp.Model.User;
import com.portalapp.portalapp.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class RegisterServices {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder the_encoder;

    public RegisterServices(UserRepository userRepository, BCryptPasswordEncoder the_encoder) {
        this.userRepository = userRepository;
        this.the_encoder = the_encoder;
    }

    public User registerUser(User user) {
        user.setPassword(the_encoder.encode(user.getPassword())); // Encrypt the password
        return userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return the_encoder.matches(rawPassword, encodedPassword); // Check password match
    }
}