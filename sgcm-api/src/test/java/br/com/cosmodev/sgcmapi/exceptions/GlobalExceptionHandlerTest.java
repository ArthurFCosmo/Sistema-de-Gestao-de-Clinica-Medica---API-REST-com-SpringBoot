package br.com.cosmodev.sgcmapi.exceptions;

import br.com.cosmodev.sgcmapi.dtos.ExceptionTratadaResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Mock
    private HttpServletRequest request;

    @Test
    void deveMontarDtoCorretamenteParaElementoNaoEncontradoException () {

        // Preparação: Mockando a URL da requisção que será puxada utilizando métodos dentro do métodó de formatar a exception
        when(request.getRequestURL())
                .thenReturn(new StringBuffer("http://localhost:8080/especialidades/999"));

        // Preparação: Mockando a exception levando em conta que deu tudo certo na parte do service ao montá-la
        ElementoNaoEncontradoException exception = new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id 999");

        // Execução: Chamando o métodó dentro do GlobalExceptionHandler
        ResponseEntity<ExceptionTratadaResponseDto> resultado = globalExceptionHandler.tratarElementoNaoEncontradoException(request, exception);

        // Confirmação
        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode()), // Confirma se o status passado foi no cabeçalho da requisição
                () -> assertNotNull(resultado.getBody()), // Confirma que o body foi anexado na response
                () -> assertNotNull(resultado.getBody().timestamp()), // Confirma que o timestamp foi anexado ao body da response
                () -> assertEquals(404, resultado.getBody().status()), // Confirma que o status foi passado corretamente no body da response
                () -> assertEquals("Not Found", resultado.getBody().error()), // Confirma o nome do erro no body da response
                () -> assertEquals("Não foi encontrada especialidade com o id 999", resultado.getBody().message()), // Confirma a mensagem
                () -> assertEquals("http://localhost:8080/especialidades/999", resultado.getBody().path()) // Confirma o endereço endpoint
        );


    }

}
