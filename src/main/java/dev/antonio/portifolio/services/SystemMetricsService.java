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

    @Transactional
    public SystemMetricsEntity getStats() {

        Optional<SystemMetricsEntity> optionalMetrics  = metricsRepository.findById(1L);

        if (optionalMetrics.isPresent()) {
            return optionalMetrics.get();
        } else {
            SystemMetricsEntity newMetrics = new SystemMetricsEntity();
            return  metricsRepository.save(newMetrics);
        }
    }

    @Transactional
    public void incementCvDownload(String ip) {
        checkAndInitialize();
        metricsRepository.incrementCvDownload();
        adminNotification.notifyCvDownload(ip);
    }

    @Transactional
    public void incrementLabAccess(String contact) {
        checkAndInitialize();
        metricsRepository.incrementLabAccess();
        adminNotification.notifyLabAccess(contact);
    }

    @Transactional
    public void incrementApiTest(String contact) {
        checkAndInitialize();
        metricsRepository.incrementApiTest();
        adminNotification.notifyApiTest(contact);
    }

    private void checkAndInitialize() {
        boolean exists = metricsRepository.existsById(1L);

        if (!exists) {
            SystemMetricsEntity initial = new SystemMetricsEntity();
            metricsRepository.save(initial);
        }
    }
}
