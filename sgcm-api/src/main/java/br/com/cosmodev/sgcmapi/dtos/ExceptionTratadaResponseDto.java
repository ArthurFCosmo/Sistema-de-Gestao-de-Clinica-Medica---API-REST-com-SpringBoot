package br.com.cosmodev.sgcmapi.dtos;

import java.time.LocalDateTime;

public record ExceptionTratadaResponseDto (


        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path

){
}
