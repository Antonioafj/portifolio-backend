package dev.antonio.portifolio.controllers;

import dev.antonio.portifolio.entities.SystemMetricsEntity;
import dev.antonio.portifolio.services.SystemMetricsService;
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
    public ResponseEntity<Void> registerDownload() {
        metricsService.incementCvDownload();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<SystemMetricsEntity> getStats() {
        return ResponseEntity.ok(metricsService.getStats());
    }
}
