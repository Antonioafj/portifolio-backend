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

    @PostMapping("/cv-download")
    public ResponseEntity<Void> registerDownload(jakarta.servlet.http.HttpServletRequest request) {
        // Tenta pegar o IP real repassado pelo Proxy/Nginx
        String ipOriginal = request.getHeader("X-Forwarded-For");

        if (ipOriginal != null && !ipOriginal.isEmpty()) {
            // Se houver múltiplos IPs, o primeiro é sempre o do cliente real
            ipOriginal = ipOriginal.split(",")[0].trim();
        } else {
            // Se não houver Header, usa o IP da conexão direta
            ipOriginal = request.getRemoteAddr();
        }

        metricsService.incementCvDownload(ipOriginal);
        return ResponseEntity.ok().build();
    }

    
    @GetMapping("/stats")
    public ResponseEntity<SystemMetricsEntity> getStats() {
        return ResponseEntity.ok(metricsService.getStats());
    }
}
