package com.portalapp.portalapp.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.portalapp.portalapp.Model.KeyFurnizori;
import com.portalapp.portalapp.Repository.KeyFurnizoriRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/appreg")
public class TestMessageController {

    // Inject the repository at the class level, not in the method
    @Autowired
    private KeyFurnizoriRepository keyFurnizoriRepository;

    @PostMapping("/process")
    public ResponseEntity<String> processMessage(@RequestBody Map<String, String> payload) {
        try {
            // Validate the payload
            if (payload == null || !payload.containsKey("type")) {
                return new ResponseEntity<>("Invalid payload: Missing 'type' field", HttpStatus.BAD_REQUEST);
            }

            String type = payload.get("type");

            switch (type) {
                case "test":
                    return handleTestMessage(payload);
                case "reg":
                    return handleRegMessage(payload);
                default:
                    return new ResponseEntity<>("Unknown message type: " + type, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> handleTestMessage(Map<String, String> payload) {
        String testMessage = payload.get("testMessage");

        // Log the received test message
        System.out.println("Received test message: " + testMessage);

        // Validate the test message
        if (testMessage == null || testMessage.isBlank()) {
            return new ResponseEntity<>("Invalid test message received", HttpStatus.BAD_REQUEST);
        }

        // Acknowledge the test message
        return new ResponseEntity<>("Test message received successfully", HttpStatus.OK);
    }

    private ResponseEntity<String> handleRegMessage(Map<String, String> payload) {
        String name = payload.get("name");
        String secret = payload.get("secret");

        // Log the received registration message
        System.out.println("Received registration message: Name = " + name + ", Secret = " + secret);

        // Validate the name and secret
        if (name == null || name.isBlank() || secret == null || secret.isBlank()) {
            return new ResponseEntity<>("Invalid registration data received", HttpStatus.BAD_REQUEST);
        }

        // Create a new KeyFurnizori object and set the name and secret
        KeyFurnizori aux = new KeyFurnizori();
        aux.setNume(name);
        aux.setSecret(secret);

        // Store the registration info in the database using the repository
        keyFurnizoriRepository.save(aux);

        // For now, just log the received data
        System.out.println("Storing registration info: Name = " + name + ", Secret = " + secret);

        // Acknowledge the registration message
        return new ResponseEntity<>("Registration data received successfully", HttpStatus.OK);
    }
}
