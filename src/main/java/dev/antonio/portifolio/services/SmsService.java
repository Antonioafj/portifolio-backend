package dev.antonio.portifolio.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("smsService")
public class SmsService implements NotiificationService{

    @Value("${TWILIO_ACCOUNT_SID}")
    private String accountSid;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String authToken;

    @Value("${TWILIO_PHONE_NUMBER}")
    private String fromNumber;


    @Override
    public void send(String destination, String subject, String body) {
        Twilio.init(accountSid, authToken);

        String formattedDestination = destination.startsWith("+") ? destination : "+" + destination;

        Message.creator(
                new PhoneNumber(formattedDestination),
                new PhoneNumber(fromNumber),
                body

        ).create();

        System.out.println("SMS enviado via Twilio para: " + formattedDestination);
    }
}
