package dev.antonio.portifolio.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyCodeDto(

        @NotBlank(message = "O destino não pode estar vazio")
        /* A Regex abaixo aceita:
           1. Formato de e-mail padrão
           2. Números de telefone (apenas dígitos) entre 8 e 15 caracteres
        */
        @Pattern(
                regexp = "^([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})|(\\d{8,15})$",
                message = "O destino deve ser um e-mail válido ou um número de telefone (apenas números)"
        )
        String destination,

        @NotBlank(message = "O código é obrigatório")
        @Size(min = 6, max = 6, message = "O código deve ter exatamente 6 dígitos")
        String code

) {
}
