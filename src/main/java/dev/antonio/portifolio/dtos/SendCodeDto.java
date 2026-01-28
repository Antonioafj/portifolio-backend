package dev.antonio.portifolio.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendCodeDto(
        @NotBlank(message = "O e-mail/destino não pode estar vazio")
        @Email(message = "Formato de email inválido")
        String destination
) {
}
