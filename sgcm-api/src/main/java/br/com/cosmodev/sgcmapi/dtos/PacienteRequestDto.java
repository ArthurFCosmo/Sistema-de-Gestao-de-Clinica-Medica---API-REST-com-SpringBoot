package br.com.cosmodev.sgcmapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record PacienteRequestDto(

        @NotBlank(message = "Nome não pode ser nulo ou em branco.")
        String nome,

        @NotBlank(message = "CPF não pode ser nulo ou em branco.")
        @CPF(message = "CPF inválido.")
        String cpf,

        @NotBlank(message = "E-mail não pode ser nulo ou em branco.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Telefone não pode ser nulo ou em branco.")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX."
        )
        String telefone,

        @PastOrPresent(message = "Data de nascimento não pode ser futura.")
        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull(message = "Data de nascimento não pode ser nula")
        LocalDate dataNascimento

) {
}
