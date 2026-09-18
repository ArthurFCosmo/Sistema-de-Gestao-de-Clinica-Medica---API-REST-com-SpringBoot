package br.com.cosmodev.sgcmapi.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MedicoRequestDto(

        @NotBlank(message = "Nome não pode ser nulo ou em branco.")
        String nome,

        @Pattern(
                regexp = "^\\d{4,6}-[A-Z]{2}$",
                message = "CRM inválido. Use o formato NNNNNN-UF (ex: 123456-PE)."
        )
        @NotBlank(message = "CRM não pode ser nulo ou em branco.")
        String crm,

        @Email
        @NotBlank(message = "E-mail não pode ser nulo ou em branco.")
        String email,

        @Pattern(
                regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX."
        )
        @NotBlank(message = "Telefone não pode ser nulo ou em branco.")
        String telefone,

        @NotNull(message = "Id da especialidade é obrigatório.")
        Long idEspecialidade






) {
}
