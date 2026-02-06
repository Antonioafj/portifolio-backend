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

    // Constante com o seu e-mail para centralizar os logs
    private final String MEU_EMAIL = "antonioafj.edu@gmail.com";

    /**
     * Notifica quando alguém baixa seu currículo.
     * @Async: Executa em uma thread separada para não travar a resposta do usuário enquanto busca o IP.
     */
    @Async
    public void notifyCvDownload(String ip) {
        // Tenta descobrir de onde vem o IP antes de notificar
        String localizacao = buscarLocalidade(ip);

        // Envia para o Webhook do Discord (rápido e visual)
        discordService.sendDownloadNotification(
                "📄 **Download de CV:** Alguém de **" + localizacao + "** baixou seu currículo agora!"
        );

        // Envia um e-mail como log de backup
        emailService.send(MEU_EMAIL, "LOG: Download de Currículo",
                "Um visitante de " + localizacao + " clicou no botão de download do PDF.");
    }

    /**
     * Faz uma requisição externa para a API ip-api.com para geolocalizar o visitante.
     */
    private String buscarLocalidade(String ip) {
        // Filtra IPs de teste ou de rede interna (Docker) para evitar chamadas inúteis à API
        if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1") || ip.startsWith("172.")) {
            return "Ambiente Interno/Localhost";
        }

        try {
            // URL da API externa buscando apenas os campos necessários (cidade, região, país)
            String url = "http://ip-api.com/json/" + ip + "?fields=status,city,regionName,country";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // Verifica se a API retornou sucesso ("success")
            if (response != null && "success".equals(response.get("status"))) {
                return String.format("%s, %s - %s",
                        response.get("city"),
                        response.get("regionName"),
                        response.get("country"));
            }
        } catch (Exception e) {
            // Se a API falhar ou cair, o sistema não quebra, apenas retorna o IP bruto
            return "Localização Indisponível (IP: " + ip + ")";
        }
        return "Localização Desconhecida";
    }

    /**
     * Notifica quando alguém valida o código OTP e entra na área restrita.
     */
    @Async
    public void notifyLabAccess(String contact) {
        discordService.sendLabAccessNotification("🔐 **Acesso Lab:** O usuário [" + contact + "] entrou no Laboratório.");

        emailService.send(MEU_EMAIL, "LOG: Novo Acesso ao Lab", "O visitante com identificação " + contact + " validou o OTP.");
    }

    /**
     * Notifica sobre testes manuais de integração de e-mail.
     * Nota: Este não é @Async, provavelmente para garantir a ordem de log em testes.
     */
    public void notifyApiTest(String destination) {
        discordService.sendApiTestNotification("⚙️ **Teste de API:** Disparo de e-mail de teste realizado para: " + destination);

        emailService.send(MEU_EMAIL, "LOG: Teste de Integração", "O template de e-mail foi disparado com sucesso para " + destination);
    }
}