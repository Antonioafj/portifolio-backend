package dev.antonio.portifolio.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SendCodeDto(
        @NotBlank(message = "O destino não pode estar vazio")
        /* A Regex abaixo aceita:
           1. Formato de e-mail padrão
           2. Números de telefone (apenas dígitos) entre 8 e 15 caracteres
        */
        @Pattern(
                regexp = "^([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})|(\\d{8,15})$",
                message = "O destino deve ser um e-mail válido ou um número de telefone (apenas números)"
        )
        String destination
) {
}