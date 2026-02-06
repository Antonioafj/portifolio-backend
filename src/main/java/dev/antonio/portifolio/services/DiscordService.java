package dev.antonio.portifolio.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DiscordService {

    // --- CONFIGURAÇÕES VIA VARIÁVEIS DE AMBIENTE ---
    // O Spring busca essas URLs no application.properties ou variáveis do sistema.
    // Isso é seguro pois evita expor os links dos Webhooks diretamente no código (GitHub).

    @Value("${DISCORD_WEBHOOK_DOWNLOADS}")
    private String WEBHOOK_DOWNLOADS;

    @Value("${DISCORD_WEBHOOK_LAB}")
    private String WEBHOOK_LAB;

    @Value("${DISCORD_WEBHOOK_TESTES}")
    private String WEBHOOK_TESTES;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Envia notificação para o canal de Downloads de CV.
     */
    @Async // Executa em paralelo para não atrasar o fluxo principal do usuário
    public void sendDownloadNotification(String text) {
        executePost(WEBHOOK_DOWNLOADS, text);
    }

    /**
     * Envia notificação para o canal de acessos ao Laboratório.
     */
    @Async
    public void sendLabAccessNotification(String text) {
        executePost(WEBHOOK_LAB, text);
    }

    /**
     * Envia notificação para o canal de testes de API.
     */
    @Async
    public void sendApiTestNotification(String text) {
        executePost(WEBHOOK_TESTES, text);
    }

    /**
     * Método genérico que realiza o envio real para o Discord.
     * @param url A URL do Webhook destino.
     * @param text A mensagem que aparecerá no Discord.
     */
    private void executePost(String url, String text) {
        try {
            // 1. Configurar Cabeçalhos: Informa ao Discord que estamos enviando um JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Montar o Corpo: O Discord exige um campo chamado "content" na requisição
            Map<String, String> body = new HashMap<>();
            body.put("content", text);

            // 3. Criar a Entidade: Junta os dados (body) com as configurações (headers)
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // 4. Enviar: Faz o disparo HTTP POST para a URL do Webhook
            restTemplate.postForEntity(url, request, String.class);

        } catch (Exception e) {
            // Tratamento de erro silencioso para que uma falha no Discord não quebre o sistema
            System.err.println("Erro ao notificar Discord: " + e.getMessage());
        }
    }
}