package br.com.cosmodev.sgcmapi.dtos;

import jakarta.validation.constraints.NotBlank;

public record EspecialidadeRequestDto(

        @NotBlank(message = "Nome da especialidade é obrigatório.")
        String nome,

        String descricao

) {
}
