package br.com.cosmodev.sgcmapi.dtos;

public record PacienteResponseDto (

        Long id,

        String nome,

        String cpfMascarado,

        String email,

        String telefone,

        Integer idade,

        Boolean ativo

) {
}
