package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.service.EspecialidadeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EspecialidadeController.class)
public class EspecialidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EspecialidadeService especialidadeService;

    // TESTANDO OS ENDPOINTS -------------------------------------------------------------------------------------------

    @Test
    void deveSalvarEspecialidadeERetornar201() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição
        String requestBody = """
                {
                    "nome": "Cardiologista",
                    "descricao": "Especialista na saúde do coração e do sistema circulatório."
                }
                """;

        // PREPARAÇÃO: Mockando o service para retornar o ResponseDto genérico
        when(especialidadeService.salvarEspecialidade(any())).thenReturn(respostaFalsa());

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/especialidade").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated()) // Verificando o status na resposta da request
                .andExpect(jsonPath("$.id").value(1)) // Verificando o JSON do ResponseDTO na resposta da request
                .andExpect(jsonPath("$.nome").value("Cardiologista")); // Verificando o JSON do ResponseDTO na resposta da request

        // CONFIRMAÇÃO: Se o controller passou os dados corretos para o service
        verify(especialidadeService).salvarEspecialidade(any(EspecialidadeRequestDto.class));

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private EspecialidadeResponseDto respostaFalsa() {
        return new EspecialidadeResponseDto(
                1L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );
    }

}
