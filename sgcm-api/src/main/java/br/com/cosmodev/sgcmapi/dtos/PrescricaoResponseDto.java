package br.com.cosmodev.sgcmapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PrescricaoResponseDto(

        Long id,

        Long idConsulta,

        String nomeMedico,

        String crmMedico,

        String nomePaciente,

        LocalDateTime dataHoraConsulta,

        String descricao,

        String medicamentos,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataEmissao

) {
}
