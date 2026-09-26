package br.com.cosmodev.sgcmapi.exceptions;

import br.com.cosmodev.sgcmapi.dtos.ExceptionTratadaResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ElementoNaoEncontradoException.class)
    public ResponseEntity<ExceptionTratadaResponseDto> tratarElementoNaoEncontradoException (HttpServletRequest request, ElementoNaoEncontradoException exception) {
        return montarExceptionTratadaResponseDto(request, HttpStatus.NOT_FOUND, exception.getMessage());
    }

    // Métodó para criar o DTO response e evitar boilerplate
    private ResponseEntity<ExceptionTratadaResponseDto> montarExceptionTratadaResponseDto(HttpServletRequest request, HttpStatus status, String mensagem) {
        ExceptionTratadaResponseDto dtoException = new ExceptionTratadaResponseDto(

                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURL().toString()

        );

        return ResponseEntity.status(status).body(dtoException);
    }
}
