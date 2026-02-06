package dev.antonio.portifolio.controllers;

import dev.antonio.portifolio.dtos.SendCodeDto;
import dev.antonio.portifolio.dtos.TokenResponseDto;
import dev.antonio.portifolio.dtos.VerifyCodeDto;
import dev.antonio.portifolio.security.JwtToken;
import dev.antonio.portifolio.services.LabServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Define que esta classe é um controlador REST (retorna JSON/Dados)
@RequestMapping("/api/v1/auth") // Define a rota base para todos os endpoints deste arquivo
@RequiredArgsConstructor // Gera o construtor para os campos 'final' (Injeção de Dependência)
public class LabController {

    private final LabServices labServices;
    private final JwtToken jwtToken; // Componente para geração de tokens JWT

    /**
     * Endpoint para solicitar o envio do código.
     * Rota: POST /api/v1/auth/send-code
     */
    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@Valid @RequestBody SendCodeDto codeDto) {
        // Chama o serviço para processar o destino, gerar o código e enviar
        labServices.receiveContact(codeDto);

        // Retorna HTTP 200 OK
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para validar o código digitado pelo usuário.
     * Se válido, retorna um token JWT de acesso.
     * Rota: POST /api/v1/auth/verify-code
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeDto verifyCodeDto) {
        // Tenta validar o código no banco de dados (validação de 5 minutos e correspondência)
        boolean isValid = labServices.validateCode(verifyCodeDto.destination(), verifyCodeDto.code());

        if (isValid) {
            // Se o código for válido, prepara o DTO para gerar o token
            SendCodeDto dto = new SendCodeDto(verifyCodeDto.destination());

            // Gera o token JWT (autenticação via stateless)
            String token = jwtToken.generateToken(dto);

            // Retorna o token dentro de um objeto TokenResponseDto (JSON)
            return ResponseEntity.ok(new TokenResponseDto(token));
        } else {
            // Se falhar (código errado ou expirado), retorna 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido ou expirado.");
        }
    }

    /**
     * Endpoint de teste para envio de e-mail com template HTML.
     * Rota: POST /api/v1/auth/test-template
     */
    @PostMapping("/test-template")
    public ResponseEntity<Void> testTemplate(@Valid @RequestBody SendCodeDto dto) {
        // Chama o serviço que possui o controle de Rate Limit (30 segundos)
        labServices.sendFormatedEmail(dto);

        return ResponseEntity.ok().build();
    }
}