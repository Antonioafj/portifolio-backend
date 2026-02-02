package dev.antonio.portifolio.services;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final DiscordService discordService;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    private final String MEU_EMAIL = "antonioafj.edu@gmail.com";

    @Async
    public void notifyCvDownload(String ip) {
        String localizacao = buscarLocalidade(ip);

        discordService.sendDownloadNotification(
                "📄 **Download de CV:** Alguém de **" + localizacao + "** baixou seu currículo agora!"
        );

        emailService.send(MEU_EMAIL, "LOG: Download de Currículo",
                "Um visitante de " + localizacao + " clicou no botão de download do PDF.");
    }

    private String buscarLocalidade(String ip) {
        // Verifica se é IP local ou se o IP veio da rede interna do Docker (172.x)
        if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1") || ip.startsWith("172.")) {
            return "Ambiente Interno/Localhost";
        }

        try {
            // Removi o "demo." pois o ip-api padrão aceita requisições HTTP/HTTPS normalmente
            String url = "http://ip-api.com/json/" + ip + "?fields=status,city,regionName,country";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // CORREÇÃO: O status correto retornado pela API é "success" (com dois 'c' e dois 's')
            if (response != null && "success".equals(response.get("status"))) {
                return String.format("%s, %s - %s",
                        response.get("city"),
                        response.get("regionName"),
                        response.get("country"));
            }
        } catch (Exception e) {
            return "Localização Indisponível (IP: " + ip + ")";
        }
        return "Localização Desconhecida";
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
