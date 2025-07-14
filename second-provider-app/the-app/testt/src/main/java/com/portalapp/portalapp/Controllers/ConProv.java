package com.portalapp.portalapp.Controllers;

import com.portalapp.portalapp.Repository.KeyFurnizoriRepository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;


@RestController
@RequestMapping("/api/users")
public class ConProv {
    @Autowired
    KeyFurnizoriRepository keyFurnizoriRepository;
    @PostMapping("/conprov")
public ResponseEntity<String> connecToProvider(@RequestBody String data) {
    String myname = "PortalulMeu";
    try {

        String cleanData = data.replace("\"", "").trim();

        String secret = keyFurnizoriRepository.findSecretByNume(cleanData);
        
        if (secret == null) {
            return new ResponseEntity<>("Secret not found for provider: " + cleanData, HttpStatus.BAD_REQUEST);
        }

        String message = myname + "," + secret;
        System.out.println("Message being sent: " + message);

        String url = switch (cleanData) {
            case "Furnizor1" -> "http://localhost:7501/api/special/extlogin";
            case "Furnizor2" -> "http://localhost:7503/api/special/extlogin";
            case "Furnizor3" -> "http://localhost:7777/api/special/extlogin";
            default -> throw new IllegalArgumentException("Unknown provider");
        };

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        // Send the message to the provider backend
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Extract login URL from provider's response
        String loginPageUrl = response.getBody();
        System.out.println(loginPageUrl);
        return new ResponseEntity<>(loginPageUrl, HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>("Error trying to link with the provider", HttpStatus.BAD_REQUEST);
    }
}




}
