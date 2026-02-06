package dev.antonio.portifolio.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("smsService") // Nomeia o Bean para que o @Qualifier("smsService") no LabServices funcione
public class SmsService implements NotiificationService {

    // --- CREDENCIAIS DO TWILIO ---
    // Injetadas via variáveis de ambiente por segurança
    @Value("${TWILIO_ACCOUNT_SID}")
    private String accountSid;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String authToken;

    @Value("${TWILIO_PHONE_NUMBER}") // O número virtual que o Twilio te forneceu
    private String fromNumber;

    @Override
    public void send(String destination, String subject, String body) {
        // 1. Inicializa o cliente do Twilio com suas credenciais
        Twilio.init(accountSid, authToken);

        // 2. Normalização do número (E.164)
        // O Twilio exige que o número comece com '+'.
        // Como o LabServices já adiciona o '55', aqui apenas garantimos o sinal de mais.
        String formattedDestination = destination.startsWith("+") ? destination : "+" + destination;

        // 3. Criação e disparo da mensagem
        // Note que o 'subject' (assunto) é ignorado, pois SMS padrão só possui o corpo (body)
        Message.creator(
                new PhoneNumber(formattedDestination), // Destinatário
                new PhoneNumber(fromNumber),           // Remetente (Seu número Twilio)
                body                                   // Conteúdo do SMS
        ).create();

        // Log simples para console (útil para debug em desenvolvimento)
        System.out.println("SMS enviado via Twilio para: " + formattedDestination);
    }
}