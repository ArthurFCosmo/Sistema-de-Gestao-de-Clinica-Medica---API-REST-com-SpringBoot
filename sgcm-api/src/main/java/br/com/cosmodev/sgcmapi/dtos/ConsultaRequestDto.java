package br.com.cosmodev.sgcmapi.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaRequestDto(

        @NotNull(message = "Data e hora da consulta são obrigatórias.")
        @Future(message = "Consulta deve ser no futuro.")
        LocalDateTime dataHora,

        String observacoes,

        @NotNull(message = "Id do médico é obrigatório.")
        Long idMedico,

        @NotNull(message = "Id do paciente é obrigatório.")
        Long idPaciente

) {
}
