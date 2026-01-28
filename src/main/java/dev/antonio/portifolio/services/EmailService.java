package dev.antonio.portifolio.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("emailService")
@RequiredArgsConstructor
public class EmailService implements NotiificationService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String destination, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // O parâmetro 'true' indica que a mensagem terá várias partes (multipart), necessário para HTML
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("antonioafj.edu@gmail.com");
            helper.setTo(destination);
            helper.setSubject(subject);

            // O segundo parâmetro 'true' é o segredo: ele avisa ao Gmail que o conteúdo é HTML
            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Tratamento de erro para falha na construção da mensagem
            throw new RuntimeException("Erro ao formatar e-mail HTML", e);
        }
    }
}
