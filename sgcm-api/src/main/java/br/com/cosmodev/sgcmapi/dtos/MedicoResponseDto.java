package br.com.cosmodev.sgcmapi.dtos;

public record MedicoResponseDto(

        Long id,

        String nome,

        String crm,

        String email,

        String telefone,

        Boolean ativo,

        String especialidade

) {
}
