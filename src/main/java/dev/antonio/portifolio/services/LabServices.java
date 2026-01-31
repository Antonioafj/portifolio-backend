package dev.antonio.portifolio.services;

import dev.antonio.portifolio.dtos.SendCodeDto;
import dev.antonio.portifolio.dtos.ValidationEntityDto;
import dev.antonio.portifolio.entities.ValidationEntity;
import dev.antonio.portifolio.repositories.ValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class LabServices {

    private final Map<String, LocalDateTime> rateLimitCache = new ConcurrentHashMap<>();
    private final ValidationRepository validationRepository;
    private final TemplateService templateService;
    private final SystemMetricsService metricsService;

    @Qualifier("emailService")
    private final NotiificationService emailService;

    @Qualifier("smsService")
    private final NotiificationService smsService;

    @Transactional
    public void receiveContact(SendCodeDto dto) {
        if (dto == null || dto.destination() == null || dto.destination().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destino inválido.");
        }

        // --- UNIFICAÇÃO DA FORMATAÇÃO ---
        String processedDestination = dto.destination();
        if (!dto.destination().contains("@")) {
            processedDestination = dto.destination().replaceAll("\\D", "");
            if (processedDestination.length() <= 11) {
                processedDestination = "55" + processedDestination;
            }
        }

        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));

        // Limpa códigos anteriores usando o destino formatado
        validationRepository.deleteByDestination(processedDestination);

        // Salva no banco com o destino formatado (ex: 55...)
        ValidationEntityDto validationEntityDto = new ValidationEntityDto(processedDestination, code);
        ValidationEntity validationEntity = new ValidationEntity(validationEntityDto);
        validationRepository.save(validationEntity);

        metricsService.incrementLabAccess(processedDestination);

        String subject = "Seu Código de Verificação";
        String body = "Olá! Use o código abaixo para acessar o sistema:\n\n " + code;

        try {
            if (dto.destination().contains("@")) {
                emailService.send(processedDestination, subject, body);
            } else {
                // Envia o SMS com o número que já está formatado com 55
                smsService.send(processedDestination, subject, body);
            }
        } catch (Exception e) {
            System.err.println("Erro crítico no envio: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao enviar o código.");
        }
    }

    @Transactional
    public boolean validateCode(String destination, String userCode) {
        if (destination == null || userCode == null) return false;

        String processedDestination = destination;
        if (!destination.contains("@")) {
            processedDestination = destination.replaceAll("\\D", "");
            if (processedDestination.length() <= 11) {
                processedDestination = "55" + processedDestination;
            }
        }

        // Agora a busca funciona pois ambos (save e find) usam o "55"
        Optional<ValidationEntity> optionalEntity = validationRepository.findByDestination(processedDestination);

        if (optionalEntity.isPresent()) {
            ValidationEntity entity = optionalEntity.get();
            LocalDateTime expireAt = entity.getCreatedAt().plusMinutes(5);

            if (LocalDateTime.now().isAfter(expireAt)) {
                validationRepository.deleteByDestination(processedDestination);
                return false;
            }

            if (entity.getCode().equals(userCode)) {
                validationRepository.deleteByDestination(processedDestination);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void sendFormatedEmail(SendCodeDto dto) {
        if (dto == null || dto.destination() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail de destino nulo.");
        }

        String destination = dto.destination();
        LocalDateTime agora = LocalDateTime.now();

        if (rateLimitCache.containsKey(destination)) {
            LocalDateTime ultimaVez = rateLimitCache.get(destination);
            if (ultimaVez.plusSeconds(30).isAfter(agora)) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Aguarde 30 segundos.");
            }
        }

        rateLimitCache.put(destination, agora);
        metricsService.incrementApiTest(destination);

        String htmlContent = templateService.generateTemplate("Visitante");
        String subject = "Teste de Template HTML - Portfólio";

        emailService.send(destination, subject, htmlContent);
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanRateLimitCache() {
        LocalDateTime limiteSuperior = LocalDateTime.now().minusMinutes(5);
        rateLimitCache.entrySet().removeIf(entry -> entry.getValue().isBefore(limiteSuperior));
        System.out.println("Faxina no RateLimitCache concluída!");
    }
}