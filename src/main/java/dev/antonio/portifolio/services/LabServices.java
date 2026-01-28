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
        // 1. CORREÇÃO: Validação defensiva para evitar NullPointerException
        if (dto == null || dto.destination() == null || dto.destination().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destino inválido.");
        }

        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));

        // Limpa códigos anteriores para o mesmo destino
        validationRepository.deleteByDestination(dto.destination());

        ValidationEntityDto validationEntityDto = new ValidationEntityDto(dto.destination(), code);
        ValidationEntity validationEntity = new ValidationEntity(validationEntityDto);
        validationRepository.save(validationEntity);

        metricsService.incrementLabAccess(dto.destination());

        String subject = "Seu Código de Verificação";
        String body = "Olá! Use o código abaixo para acessar o sistema:\n\n " + code;

        // 2. Lógica de envio
        if (dto.destination().contains("@")) {
            emailService.send(dto.destination(), subject, body);
        } else {
            smsService.send(dto.destination(), subject, body);
        }
    }

    @Transactional
    public boolean validateCode(String destination, String userCode) {
        if (destination == null || userCode == null) return false;

        Optional<ValidationEntity> optionalEntity = validationRepository.findByDestination(destination);

        if (optionalEntity.isPresent()) {
            ValidationEntity entity = optionalEntity.get();
            LocalDateTime expireAt = entity.getCreatedAt().plusMinutes(5);

            if (LocalDateTime.now().isAfter(expireAt)) {
                validationRepository.deleteByDestination(destination);
                return false;
            }

            if (entity.getCode().equals(userCode)) {
                validationRepository.deleteByDestination(destination);
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

        // 3. CORREÇÃO: Verificação de Rate Limit
        if (rateLimitCache.containsKey(destination)) {
            LocalDateTime ultimaVez = rateLimitCache.get(destination);
            if (ultimaVez.plusSeconds(30).isAfter(agora)) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Aguarde 30 segundos para um novo envio.");
            }
        }

        // 4. ATUALIZAÇÃO: Registrar o momento do envio no cache
        rateLimitCache.put(destination, agora);

        metricsService.incrementApiTest(destination);

        String htmlContent = templateService.generateTemplate("Visitante");
        String subject = "Teste de Template HTML - Portfólio";

        emailService.send(destination, subject, htmlContent);
    }

    @Scheduled(fixedRate = 3600000) // 1 hora
    public void cleanRateLimitCache() {
        LocalDateTime limiteSuperior = LocalDateTime.now().minusMinutes(5);
        rateLimitCache.entrySet().removeIf(entry -> entry.getValue().isBefore(limiteSuperior));
        System.out.println("Faxina no RateLimitCache concluída!");
    }
}