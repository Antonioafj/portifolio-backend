package dev.antonio.portifolio.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyCodeDto(

        @NotBlank(message = "O e-mail de destino é obrigatório")
        @Email(message = "Formato de email inválido")
        String destination,

        @NotBlank(message = "O código é obrigatório")
        @Size(min = 6, max = 6, message = "O código deve ter exatamente 6 dígitos")
        String code

) {
}
