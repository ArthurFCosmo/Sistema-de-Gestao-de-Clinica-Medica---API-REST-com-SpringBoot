package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.service.EspecialidadeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EspecialidadeController.class)
public class EspecialidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EspecialidadeService especialidadeService;

    // TESTANDO OS ENDPOINTS (POST "/especialidade") -------------------------------------------------------------------

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

    @Test
    void deveLancarMethodArgumentNotValidExceptionERetornar400QuandoOBodyEstiverInvalido() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (nome em branco)
        String requestBody = """
                {
                    "nome": "",
                    "descricao": "Especialista na saúde do coração e do sistema circulatório."
                }
                """;

        mockMvc.perform(post("/especialidade").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(especialidadeService, never()).salvarEspecialidade(any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformado() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBody = """
            {
                "nome": "Cardiologista",
                "descricao": "Especialista na saúde do coração
            """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/especialidade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        HttpMessageNotReadableException.class,
                        result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(especialidadeService, never()).salvarEspecialidade(any());

    }

    @Test
    void deveRetornar415QuandoContentTypeNaoForEnviado() throws Exception {

        // EXECUÇÃO E CONFIRMAÇÃO: requisição sem contentType
        mockMvc.perform(post("/especialidade")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        // CONFIRMAÇÃO: nada chegou ao service
        verify(especialidadeService, never()).salvarEspecialidade(any());

    }

    // TESTANDO OS ENDPOINTS (GET "/especialidade") --------------------------------------------------------------------

    @Test
    void deveRetornarListaComTodasEspecialidadesEStatus200() throws Exception {

        // PREPARAÇÃO: Retornando lista com todas as especialidades
        when(especialidadeService.listarEspecialidades()).thenReturn(listaDeRespostasFalsas());

        mockMvc.perform(get("/especialidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Cardiologista"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Oncologista"));

        verify(especialidadeService).listarEspecialidades();

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private List<EspecialidadeResponseDto> listaDeRespostasFalsas() {
        return List.of(
                new EspecialidadeResponseDto(1L, "Cardiologista", "Especialista na saúde do coração e do sistema circulatório."),
                new EspecialidadeResponseDto(2L, "Oncologista", "Especialista em diagnóstico e tratamentos de câncer.")
        );
    }

    private EspecialidadeResponseDto respostaFalsa() {
        return new EspecialidadeResponseDto(
                1L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );
    }

}
