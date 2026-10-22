package br.com.cosmodev.sgcmapi.exceptions;

import br.com.cosmodev.sgcmapi.dtos.ExceptionTratadaResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ElementoNaoEncontradoException.class)
    public ResponseEntity<ExceptionTratadaResponseDto> tratarElementoNaoEncontradoException (HttpServletRequest request, ElementoNaoEncontradoException exception) {
        return montarExceptionTratadaResponseDto(request, HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RegraDeNegocioVioladaException.class)
    public ResponseEntity<ExceptionTratadaResponseDto> tratarRegraDeNegocioVioladaException (HttpServletRequest request, RegraDeNegocioVioladaException exception) {
        return montarExceptionTratadaResponseDto(request, HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionTratadaResponseDto> tratarMethodArgumentNotValidException (HttpServletRequest request, MethodArgumentNotValidException exception) {

        String mensagemFormatada = exception.getBindingResult().getFieldErrors() // Todos os erros de campo do @Validation são armazenados em uma lista
                .stream().map(
                        e -> e.getField() + ": " + e.getDefaultMessage() // Cada erro da lista é separado aqui e agrupado com a sua mensagem
                ).collect(Collectors.joining(", ")); // Todos os erros são juntos aqui numa string só, separados por vírgula

        return montarExceptionTratadaResponseDto(request, HttpStatus.BAD_REQUEST, mensagemFormatada);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionTratadaResponseDto> tratarDataIntegrityViolationException (HttpServletRequest request, DataIntegrityViolationException exception) {
        return montarExceptionTratadaResponseDto(request, HttpStatus.CONFLICT, "A operação viola uma regra de integridade dos dados.");
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
