package dev.antonio.portifolio.services;

import dev.antonio.portifolio.entities.SystemMetricsEntity;
import dev.antonio.portifolio.repositories.SystemMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemMetricsService {

    private final SystemMetricsRepository metricsRepository;
    private final AdminNotificationService adminNotification;

    /**
     * Recupera as estatísticas globais do sistema.
     * @return A única linha de métricas (ID 1) ou cria uma nova se não existir.
     */
    @Transactional
    public SystemMetricsEntity getStats() {
        // Busca o registro de ID 1 (Singleton no banco de dados)
        Optional<SystemMetricsEntity> optionalMetrics  = metricsRepository.findById(1L);

        if (optionalMetrics.isPresent()) {
            return optionalMetrics.get();
        } else {
            // Se o sistema acabou de subir e não tem métricas, inicializa com valores zerados
            SystemMetricsEntity newMetrics = new SystemMetricsEntity();
            return metricsRepository.save(newMetrics);
        }
    }

    /**
     * Incrementa o contador de downloads do currículo e dispara alerta.
     */
    @Transactional
    public void incementCvDownload(String ip) {
        checkAndInitialize(); // Garante que a linha 1 exista no banco
        metricsRepository.incrementCvDownload(); // Faz o update (+1) via query nativa
        adminNotification.notifyCvDownload(ip); // Inicia o processo assíncrono de notificação
    }

    /**
     * Incrementa o contador de acessos ao laboratório (OTP validado).
     */
    @Transactional
    public void incrementLabAccess(String contact) {
        checkAndInitialize();
        metricsRepository.incrementLabAccess();
        adminNotification.notifyLabAccess(contact);
    }

    /**
     * Incrementa o contador de testes de API realizados.
     */
    @Transactional
    public void incrementApiTest(String contact) {
        checkAndInitialize();
        metricsRepository.incrementApiTest();
        adminNotification.notifyApiTest(contact);
    }

    /**
     * Método auxiliar de "Auto-healing" (Auto-cura).
     * Garante que o banco de dados sempre tenha o registro necessário para o incremento.
     */
    private void checkAndInitialize() {
        boolean exists = metricsRepository.existsById(1L);

        if (!exists) {
            SystemMetricsEntity initial = new SystemMetricsEntity();
            // Salva o registro inicial com ID 1
            metricsRepository.save(initial);
        }
    }
}