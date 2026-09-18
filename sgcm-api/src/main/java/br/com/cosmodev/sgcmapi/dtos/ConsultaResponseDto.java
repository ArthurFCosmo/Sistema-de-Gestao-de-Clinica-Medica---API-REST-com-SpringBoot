package br.com.cosmodev.sgcmapi.dtos;

import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ConsultaResponseDto(

        Long id,

        @JsonFormat(pattern = "dd/MM/yy HH:mm:ss")
        LocalDateTime dataHora,

        StatusConsulta status,

        String observacoes,

        Long idMedico,

        String nomeMedico,

        String crmMedico,

        Long idPaciente,

        String nomePaciente

) {
}
