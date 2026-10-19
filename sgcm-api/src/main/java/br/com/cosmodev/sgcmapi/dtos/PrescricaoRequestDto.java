package br.com.cosmodev.sgcmapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PrescricaoRequestDto(

        @NotBlank(message = "Prescrição precisa de uma descrição")
        String descricao,

        String medicamentos,

        @NotNull(message = "Id da consulta é obrigatório.")
        Long idConsulta,

        LocalDate dataEmissao

) {
}
