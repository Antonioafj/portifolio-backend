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


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LabController {

    private final LabServices labServices;

    private final JwtToken jwtToken;

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@Valid @RequestBody SendCodeDto codeDto) {

        labServices.receiveContact(codeDto);

        return  ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeDto verifyCodeDto) {
        boolean isValid = labServices.validateCode(verifyCodeDto.destination(), verifyCodeDto.code());

        if (isValid) {
            SendCodeDto dto = new SendCodeDto(verifyCodeDto.destination());
            String token = jwtToken.generateToken(dto);
            return ResponseEntity.ok(new TokenResponseDto(token));
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido ou expirado.");

         }
    }

    @PostMapping("/test-template")
    public ResponseEntity<Void> testTemplate(@Valid @RequestBody SendCodeDto dto) {
        labServices.sendFormatedEmail(dto);
        return ResponseEntity.ok().build();
    }
}
