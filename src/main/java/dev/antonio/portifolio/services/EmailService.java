package dev.antonio.portifolio.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("emailService") // Nomeia o Bean para que o @Qualifier("emailService") funcione no LabServices
@RequiredArgsConstructor
public class EmailService implements NotiificationService {

    private final JavaMailSender mailSender; // Interface do Spring Boot para envio de e-mails

    @Override
    public void send(String destination, String subject, String body) {
        try {
            // 1. Cria o objeto base da mensagem (MimeMessage permite conteúdos complexos)
            MimeMessage message = mailSender.createMimeMessage();

            // 2. MimeMessageHelper é uma classe utilitária para facilitar a configuração do e-mail
            // O parâmetro 'true' habilita o modo multipart (necessário para anexos ou HTML)
            // "UTF-8" garante que acentos e caracteres especiais não fiquem corrompidos
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 3. Configura os detalhes do remetente e destinatário
            helper.setFrom("antonioafj.edu@gmail.com");
            helper.setTo(destination);
            helper.setSubject(subject);

            // 4. Configuração do corpo da mensagem
            // O segundo parâmetro 'true' é fundamental: ele instrui o cliente de e-mail (Gmail, Outlook)
            // a renderizar o texto como código HTML em vez de texto puro.
            helper.setText(body, true);

            // 5. Realiza o disparo através do servidor SMTP configurado no seu application.properties
            mailSender.send(message);

        } catch (MessagingException e) {
            // Se houver erro na estrutura da mensagem, lança uma exceção de runtime
            throw new RuntimeException("Erro ao formatar e-mail HTML", e);
        }
    }
}