package dev.antonio.portifolio.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service("smsService")
public class SmsService implements NotiificationService{

    private final String accountSid = System.getProperty("TWILIO_ACCOUNT_SID");
    private final String authToken = System.getProperty("TWILIO_AUTH_TOKEN");
    private final String fromNumber = System.getProperty("TWILIO_PHONE_NUMBER");


    @Override
    public void send(String destination, String subject, String body) {
        Twilio.init(accountSid, authToken);


        Message.creator(
                new PhoneNumber(destination),
                new PhoneNumber(fromNumber),
                body

        ).create();

        System.out.println("SMS enviado via Twilio para: " + destination);
    }
}
