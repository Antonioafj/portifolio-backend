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
@RequiredArgsConstructor // Cria o construtor para injeção de dependências dos campos 'final'
public class LabServices {

    // Cache em memória para controlar o tempo entre requisições (Rate Limit)
    private final Map<String, LocalDateTime> rateLimitCache = new ConcurrentHashMap<>();

    private final ValidationRepository validationRepository;
    private final TemplateService templateService;
    private final SystemMetricsService metricsService;

    // Injeta a implementação específica de e-mail do NotiificationService
    @Qualifier("emailService")
    private final NotiificationService emailService;

    // Injeta a implementação específica de SMS do NotiificationService
    @Qualifier("smsService")
    private final NotiificationService smsService;

    /**
     * Gera e envia um código de verificação para o usuário (E-mail ou SMS).
     */
    @Transactional // Garante consistência: se o envio falhar, as mudanças no banco podem ser revertidas
    public void receiveContact(SendCodeDto dto) {
        // Validação básica de entrada
        if (dto == null || dto.destination() == null || dto.destination().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destino inválido.");
        }

        // --- NORMALIZAÇÃO DO DESTINO ---
        String processedDestination = dto.destination();
        if (!dto.destination().contains("@")) {
            // Se não tem '@', assume que é telefone: remove parênteses, traços e espaços
            processedDestination = dto.destination().replaceAll("\\D", "");
            // Se for número brasileiro sem DDI, adiciona o prefixo '55'
            if (processedDestination.length() <= 11) {
                processedDestination = "55" + processedDestination;
            }
        }

        // Gera um código aleatório de 6 dígitos
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));

        // Regra de negócio: Um destino só pode ter um código ativo por vez
        validationRepository.deleteByDestination(processedDestination);

        // Salva o novo código vinculado ao destino no banco de dados
        ValidationEntityDto validationEntityDto = new ValidationEntityDto(processedDestination, code);
        ValidationEntity validationEntity = new ValidationEntity(validationEntityDto);
        validationRepository.save(validationEntity);

        // Registra métricas de acesso
        metricsService.incrementLabAccess(processedDestination);

        String subject = "Seu Código de Verificação";
        String body = "Olá! Use o código abaixo para acessar o sistema:\n\n " + code;

        try {
            // Decisão de canal de envio baseada no formato do destino
            if (dto.destination().contains("@")) {
                emailService.send(processedDestination, subject, body);
            } else {
                smsService.send(processedDestination, subject, body);
            }
        } catch (Exception e) {
            // Log de erro interno para o desenvolvedor
            System.err.println("Erro crítico no envio: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao enviar o código.");
        }
    }

    /**
     * Valida se o código inserido pelo usuário é válido e ainda não expirou.
     */
    @Transactional
    public boolean validateCode(String destination, String userCode) {
        if (destination == null || userCode == null) return false;

        // Normaliza o destino para garantir que a busca no banco bata com o formato salvo
        String processedDestination = destination;
        if (!destination.contains("@")) {
            processedDestination = destination.replaceAll("\\D", "");
            if (processedDestination.length() <= 11) {
                processedDestination = "55" + processedDestination;
            }
        }

        Optional<ValidationEntity> optionalEntity = validationRepository.findByDestination(processedDestination);

        if (optionalEntity.isPresent()) {
            ValidationEntity entity = optionalEntity.get();
            // Define validade de 5 minutos
            LocalDateTime expireAt = entity.getCreatedAt().plusMinutes(5);

            // Verifica se o código expirou
            if (LocalDateTime.now().isAfter(expireAt)) {
                validationRepository.deleteByDestination(processedDestination);
                return false;
            }

            // Verifica se o código bate com o que o usuário digitou
            if (entity.getCode().equals(userCode)) {
                // Código usado com sucesso é deletado (segurança: uso único)
                validationRepository.deleteByDestination(processedDestination);
                return true;
            }
        }
        return false;
    }

    /**
     * Envia um e-mail formatado (HTML) com proteção contra spam (Rate Limit).
     */
    @Transactional
    public void sendFormatedEmail(SendCodeDto dto) {
        if (dto == null || dto.destination() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail de destino nulo.");
        }

        String destination = dto.destination();
        LocalDateTime agora = LocalDateTime.now();

        // Lógica de Rate Limit: Verifica se o e-mail foi solicitado há menos de 30 segundos
        if (rateLimitCache.containsKey(destination)) {
            LocalDateTime ultimaVez = rateLimitCache.get(destination);
            if (ultimaVez.plusSeconds(30).isAfter(agora)) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Aguarde 30 segundos.");
            }
        }

        // Atualiza o cache com o novo horário de envio
        rateLimitCache.put(destination, agora);
        metricsService.incrementApiTest(destination);

        // Gera o conteúdo visual do e-mail usando o TemplateService
        String htmlContent = templateService.generateTemplate("Visitante");
        String subject = "Teste de Template HTML - Portfólio";

        emailService.send(destination, subject, htmlContent);
    }

    /**
     * Tarefa agendada que roda a cada 1 hora para limpar o cache de Rate Limit.
     * Remove entradas com mais de 5 minutos para evitar vazamento de memória.
     */
    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hora
    public void cleanRateLimitCache() {
        LocalDateTime limiteSuperior = LocalDateTime.now().minusMinutes(5);
        rateLimitCache.entrySet().removeIf(entry -> entry.getValue().isBefore(limiteSuperior));
        System.out.println("Faxina no RateLimitCache concluída!");
    }
}