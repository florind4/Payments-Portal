package com.portalapp.portalapp.Controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    // Inject the Twilio credentials from application.properties
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    // Initialize Twilio
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void createMsj(String msj) {
        // Ensure Twilio is initialized
        initTwilio();

        // Send the message using the Twilio API
        Message message = Message.creator(
            new com.twilio.type.PhoneNumber("whatsapp:+40753780324"),
            new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
            "NOTIFICARE: " + msj + " Verificati platforma ! ")
        .create();
    }
}
