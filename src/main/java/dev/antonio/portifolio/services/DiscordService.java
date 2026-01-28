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

    @Value("${DISCORD_WEBHOOK_DOWNLOADS}")
    private String WEBHOOK_DOWNLOADS;

    @Value("${DISCORD_WEBHOOK_LAB}")
    private String WEBHOOK_LAB;

    @Value("${DISCORD_WEBHOOK_TESTES}")
    private String WEBHOOK_TESTES;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendDownloadNotification(String text) {
        executePost(WEBHOOK_DOWNLOADS, text);
    }

    @Async
    public void sendLabAccessNotification(String text) {
        executePost(WEBHOOK_LAB, text);
    }

    @Async
    public void sendApiTestNotification(String text) {
        executePost(WEBHOOK_TESTES, text);
    }

    private void executePost(String url, String text) {
        try {
            // 1. Configurar Cabeçalhos
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Montar o Corpo
            Map<String, String> body = new HashMap<>();
            body.put("content", text);

            // 3. Criar a Entidade (Spring Entity)
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // 4. Enviar a 'request' (que contém body + headers)
            restTemplate.postForEntity(url, request, String.class);

        } catch (Exception e) {
            System.err.println("Erro ao notificar Discord: " + e.getMessage());
        }
    }
}