package dev.antonio.portifolio.controllers;

import dev.antonio.portifolio.entities.SystemMetricsEntity;
import dev.antonio.portifolio.services.SystemMetricsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class SystemMetricsController {

    private final SystemMetricsService metricsService;

    /**
     * Registra o evento de download do currículo.
     * Tenta identificar o endereço IP do usuário para métricas geográficas ou de auditoria.
     */
    @PostMapping("/cv-download")
    public ResponseEntity<Void> registerDownload(jakarta.servlet.http.HttpServletRequest request) {

        // --- LÓGICA DE CAPTURA DE IP ---
        // Em servidores reais (Nginx, Heroku, AWS), o IP do cliente vem no Header "X-Forwarded-For"
        // porque o servidor age como um proxy.
        String ipOriginal = request.getHeader("X-Forwarded-For");

        if (ipOriginal != null && !ipOriginal.isEmpty()) {
            // Se houver uma lista de IPs (ex: "IP_Cliente, IP_Proxy1"), pegamos o primeiro da esquerda.
            ipOriginal = ipOriginal.split(",")[0].trim();
        } else {
            // Caso não exista o header (ambiente local ou conexão direta), pega o IP direto da requisição.
            ipOriginal = request.getRemoteAddr();
        }

        // Incrementa o contador de downloads no serviço de métricas associando ao IP
        metricsService.incementCvDownload(ipOriginal);

        return ResponseEntity.ok().build();
    }

    /**
     * Retorna os dados estatísticos acumulados (total de acessos, downloads, etc).
     * Rota: GET /api/v1/metrics/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<SystemMetricsEntity> getStats() {
        // Busca a entidade de métricas no banco de dados através do serviço
        return ResponseEntity.ok(metricsService.getStats());
    }
}