package com.portalapp.portalapp.Controllers;

import com.portalapp.portalapp.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RegisterServices registerServices;

    // Constructor injection for RegisterServices
    public UserController(RegisterServices registerServices) {
        this.registerServices = registerServices;
    }

    // POST method to register user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            String phn = user.getPhone();
            String usnm = user.getUsername();
            String eml = user.getEmail();

            if(phn == null || phn.length() != 10){
                return new ResponseEntity<>("Phone number wrong format ! Try again !", HttpStatus.BAD_REQUEST);
            }

            for(int i = 0; i < phn.length(); ++i){
                if(!(phn.charAt(i)>='0' && phn.charAt(i)<='9')){
                    return new ResponseEntity<>("Phone number wrong format ! Try again !", HttpStatus.BAD_REQUEST);
                }
            }

            if(usnm.length() > 16 || usnm.length() < 6){
                return new ResponseEntity<>("Username must be between 6 and 16 chars long !", HttpStatus.BAD_REQUEST);
            }

            for(int i = 0; i < eml.length(); ++i){
                if(eml.indexOf('@') == -1 || eml.indexOf('.') == -1){
                    return new ResponseEntity<>("Email wrong format ! Try again !", HttpStatus.BAD_REQUEST);
                }
            }

            if(eml.length() < 10 || eml.length() > 28){
                return new ResponseEntity<>("Email must be between 10 and 28 chars long !", HttpStatus.BAD_REQUEST);
            }

            registerServices.registerUser(user);
        
            return new ResponseEntity<>("User registered successfully! Welcome to fd.quiz!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            // Send a detailed error message in case of failure
            return new ResponseEntity<>("Error registering user ...", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        try {
            String username = user.getUsername();
            String password = user.getPassword();

            // Fetch user from the database
            User existingUser = registerServices.findUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found!"));

            // Validate password
            if (!registerServices.checkPassword(password, existingUser.getPassword())) {
                return new ResponseEntity<>("Invalid credentials!", HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>("Login successful!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Login failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
