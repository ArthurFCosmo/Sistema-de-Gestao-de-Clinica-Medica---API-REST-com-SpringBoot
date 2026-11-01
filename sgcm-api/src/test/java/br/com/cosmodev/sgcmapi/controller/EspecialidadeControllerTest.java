package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.service.EspecialidadeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EspecialidadeController.class)
public class EspecialidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EspecialidadeService especialidadeService;

    // TESTANDO OS ENDPOINTS (POST "/especialidade") -------------------------------------------------------------------

    @Test
    void deveRetornar201ESalvarAEspecialidadeNoPost() throws Exception {

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
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (nome em branco)
        String requestBodyDefeituoso = """
                {
                    "nome": "",
                    "descricao": "Especialista na saúde do coração e do sistema circulatório."
                }
                """;

        mockMvc.perform(post("/especialidade").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(especialidadeService, never()).salvarEspecialidade(any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPost() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
            {
                "nome": "Cardiologista",
                "descricao": "Especialista na saúde do coração
            """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/especialidade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(
                        HttpMessageNotReadableException.class,
                        result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(especialidadeService, never()).salvarEspecialidade(any());

    }

    @Test
    void deveRetornar415QuandoContentTypeNaoForEnviadoNoPost() throws Exception {

        // EXECUÇÃO E CONFIRMAÇÃO: requisição sem contentType
        mockMvc.perform(post("/especialidade")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        // CONFIRMAÇÃO: nada chegou ao service
        verify(especialidadeService, never()).salvarEspecialidade(any());

    }

    // TESTANDO OS ENDPOINTS (GET "/especialidade") --------------------------------------------------------------------

    @Test
    void deveRetornar200EListarEspecialidadesNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista com todas as especialidades
        when(especialidadeService.listarEspecialidades()).thenReturn(listaDeRespostasFalsas());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/especialidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Cardiologista"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Oncologista"));

        verify(especialidadeService).listarEspecialidades();

    }

    @Test
    void deveRetornar200EListaVaziaNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista com todas as especialidades
        when(especialidadeService.listarEspecialidades()).thenReturn(Collections.emptyList());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/especialidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));


        verify(especialidadeService).listarEspecialidades();

    }

    // TESTANDO OS ENDPOINTS (GET "/especialidade/id") -----------------------------------------------------------------

    @Test
    void deveRetornar200EEspecialidadePorIdNoGetId() throws Exception {

        // PREPARAÇÃO
        when(especialidadeService.buscarEspecialidadePorId(any(Long.class))).thenReturn(respostaFalsa());

        mockMvc.perform(get("/especialidade/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponseDtoFalso()));

        verify(especialidadeService).buscarEspecialidadePorId(any(Long.class));

    }

    @Test
    void deveRetornar404AoNaoEncontrarEspecialidadeNoGetId() throws Exception {

        when(especialidadeService.buscarEspecialidadePorId(any(Long.class))).thenThrow(new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id 2"));

        mockMvc.perform(get("/especialidade/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

        verify(especialidadeService).buscarEspecialidadePorId(2L);

    }

    @Test
    void deveRetornar400SeOIdNaoForNumericoNoGetId() throws Exception {

        mockMvc.perform(get("/especialidade/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentTypeMismatchException.class, result.getResolvedException()));

        verify(especialidadeService, never()).buscarEspecialidadePorId(any());

    }

    // TESTANDO OS ENDPOINTS (DELETE "/especialidade/id") --------------------------------------------------------------

    @Test
    void deveRetornar204EDeletarEspecialidadeNoDelete() throws Exception {

        mockMvc.perform(delete("/especialidade/1"))
                .andExpect(status().isNoContent());

        verify(especialidadeService).deletarEspecialidade(any(Long.class));

    }

    @Test
    void deveRetornar404AoNaoEncontrarEspecialidadeNoDelete() throws Exception {

        doThrow(new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id 2"))
                .when(especialidadeService).deletarEspecialidade(2L);

        mockMvc.perform(delete("/especialidade/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));
    }

    @Test
    void deveRetornar409AoQuebrarRegraDeNegocioNoDelete() throws Exception {

        doThrow(new RegraDeNegocioVioladaException("Especialidade com id 2 não pôde ser deletada pois existem médicos vinculados a ela."))
                .when(especialidadeService).deletarEspecialidade(2L);

        mockMvc.perform(delete("/especialidade/2"))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(RegraDeNegocioVioladaException.class, result.getResolvedException()));

    }

    // TESTANDO OS ENDPOINTS (PUT "/especialidade/id") --------------------------------------------------------------

    @Test
    void deveRetornar200EAtualizarEspecialidadeNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "nome": "Cardiologista",
                    "descricao": "Especialista na saúde do coração e do sistema circulatório."
                }
                """;

        when(especialidadeService.atualizarEspecialidade(anyLong(), any(EspecialidadeRequestDto.class))).thenReturn(respostaFalsa());

        
        mockMvc.perform(put("/especialidade/1").contentType(MediaType.APPLICATION_JSON).content(requestBody)) // EXECUÇÃO
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Cardiologista"))
            .andExpect(jsonPath("$.descricao").value("Especialista na saúde do coração e do sistema circulatório."));

        verify(especialidadeService).atualizarEspecialidade(anyLong(), any(EspecialidadeRequestDto.class));
        
    }

    @Test
    void deveRetornar404AoNaoEncontrarEspecialidadeNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "nome": "Cardiologista",
                    "descricao": "Especialista na saúde do coração e do sistema circulatório."
                }
                """;
        
        when(especialidadeService.atualizarEspecialidade(anyLong(), any(EspecialidadeRequestDto.class)))
            .thenThrow(new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id 2"));

        mockMvc.perform(put("/especialidade/2").contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPut() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (nome em branco)
        String requestBodyDefeituoso = """
                {
                    "nome": "",
                    "descricao": "Especialista na saúde do coração e do sistema circulatório."
                }
                """;

        mockMvc.perform(put("/especialidade/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(especialidadeService, never()).atualizarEspecialidade(anyLong(), any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPut() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
            {
                "nome": "Cardiologista",
                "descricao": "Especialista na saúde do coração
            """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(put("/especialidade/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class,result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(especialidadeService, never()).salvarEspecialidade(any());

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private List<EspecialidadeResponseDto> listaDeRespostasFalsas() {
        return List.of(
                new EspecialidadeResponseDto(1L, "Cardiologista", "Especialista na saúde do coração e do sistema circulatório."),
                new EspecialidadeResponseDto(2L, "Oncologista", "Especialista em diagnóstico e tratamentos de câncer.")
        );
    }

    private String jsonResponseDtoFalso() {
        return """
                {
                    "nome": "Cardiologista",
                    "descricao": "Especialista na saúde do coração e do sistema circulatório."
                }
                """;
    }

    private EspecialidadeResponseDto respostaFalsa() {
        return new EspecialidadeResponseDto(
                1L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );
    }

}
