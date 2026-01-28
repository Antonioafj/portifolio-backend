package dev.antonio.portifolio.services;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final DiscordService discordService;
    private final EmailService emailService;

    private final String MEU_EMAIL = "antonioafj.edu@gmail.com";

    @Async
    public void notifyCvDownload() {

        discordService.sendDownloadNotification("📄 **Download de CV:** Alguém baixou seu currículo agora!");

        emailService.send(MEU_EMAIL, "LOG: Download de Currículo", "Um visitante clicou no botão de download do PDF.");
    }

    @Async
    public void notifyLabAccess(String contact) {
        discordService.sendLabAccessNotification("🔐 **Acesso Lab:** O usuário [" + contact + "] entrou no Laboratório.");

        emailService.send(MEU_EMAIL, "LOG: Novo Acesso ao Lab", "O visitante com identificação " + contact + " validou o OTP.");
    }

    public void notifyApiTest(String destination) {
        discordService.sendApiTestNotification("⚙️ **Teste de API:** Disparo de e-mail de teste realizado para: " + destination);

        emailService.send(MEU_EMAIL, "LOG: Teste de Integração", "O template de e-mail foi disparado com sucesso para " + destination);
    }

}
