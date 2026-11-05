package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.PrescricaoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PrescricaoResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.service.PrescricaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrescricaoController.class)
public class PrescricaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrescricaoService prescricaoService;

    // TESTANDO OS ENDPOINTS (POST "/prescricao") ---------------------------------------------------------------------

    @Test
    void deveRetornar201ESalvarAPrescricaoNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição
        String requestBody = """
                {
                    "descricao": "Tratamento para hipertensão",
                    "medicamentos": "Medicamento A, 1 comprimido ao dia",
                    "idConsulta": 1
                }
                """;

        // PREPARAÇÃO: Mockando o service para retornar o ResponseDto genérico
        when(prescricaoService.salvarPrescricao(any())).thenReturn(respostaFalsa());

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/prescricao").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Tratamento para hipertensão"));

        // CONFIRMAÇÃO: Se o controller passou os dados corretos para o service
        verify(prescricaoService).salvarPrescricao(any(PrescricaoRequestDto.class));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (descrição em branco)
        String requestBodyDefeituoso = """
                {
                    "descricao": "",
                    "medicamentos": "Medicamento A",
                    "idConsulta": 1
                }
                """;

        mockMvc.perform(post("/prescricao").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(prescricaoService, never()).salvarPrescricao(any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPost() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "descricao": "Tratamento para hipertensão
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/prescricao").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(prescricaoService, never()).salvarPrescricao(any());

    }

    @Test
    void deveRetornar415QuandoContentTypeNaoForEnviadoNoPost() throws Exception {

        // EXECUÇÃO E CONFIRMAÇÃO: requisição sem contentType
        mockMvc.perform(post("/prescricao").content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        // CONFIRMAÇÃO: nada chegou ao service
        verify(prescricaoService, never()).salvarPrescricao(any());

    }

    @Test
    void deveRetornar404AoNaoEncontrarConsultaNoPost() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "descricao": "Tratamento para hipertensão",
                    "idConsulta": 2
                }
                """;

        when(prescricaoService.salvarPrescricao(any(PrescricaoRequestDto.class)))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrada consulta com id 2"));

        mockMvc.perform(post("/prescricao").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

        verify(prescricaoService).salvarPrescricao(any(PrescricaoRequestDto.class));

    }

    @Test
    void deveRetornar409AoQuebrarRegraDeNegocioNoPost() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "descricao": "Tratamento para hipertensão",
                    "idConsulta": 2
                }
                """;

        when(prescricaoService.salvarPrescricao(any(PrescricaoRequestDto.class)))
                .thenThrow(new RegraDeNegocioVioladaException("A prescrição não pôde ser salva pois a consulta com id 2 não está descrita como realizada."));

        mockMvc.perform(post("/prescricao").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(RegraDeNegocioVioladaException.class, result.getResolvedException()));

        verify(prescricaoService).salvarPrescricao(any(PrescricaoRequestDto.class));

    }

    // TESTANDO OS ENDPOINTS (GET "/prescricao") ----------------------------------------------------------------------

    @Test
    void deveRetornar200EListarPrescricoesNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista com todas as prescrições
        when(prescricaoService.listarPrescricoes()).thenReturn(listaDeRespostasFalsas());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/prescricao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Tratamento para hipertensão"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].descricao").value("Tratamento para alergia"));

        verify(prescricaoService).listarPrescricoes();

    }

    @Test
    void deveRetornar200EListaVaziaNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista vazia de prescrições
        when(prescricaoService.listarPrescricoes()).thenReturn(Collections.emptyList());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/prescricao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(prescricaoService).listarPrescricoes();

    }

    // TESTANDO OS ENDPOINTS (GET "/prescricao/id") -------------------------------------------------------------------

    @Test
    void deveRetornar200EPrescricaoPorIdNoGetId() throws Exception {

        // PREPARAÇÃO
        when(prescricaoService.buscarPrescricaoPeloId(anyLong())).thenReturn(respostaFalsa());

        mockMvc.perform(get("/prescricao/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponseDtoFalso()));

        verify(prescricaoService).buscarPrescricaoPeloId(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarPrescricaoNoGetId() throws Exception {

        when(prescricaoService.buscarPrescricaoPeloId(2L))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrada prescrição com id 2"));

        mockMvc.perform(get("/prescricao/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

        verify(prescricaoService).buscarPrescricaoPeloId(2L);

    }

    @Test
    void deveRetornar400SeOIdNaoForNumericoNoGetId() throws Exception {

        mockMvc.perform(get("/prescricao/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentTypeMismatchException.class, result.getResolvedException()));

        verify(prescricaoService, never()).buscarPrescricaoPeloId(any());

    }

    // TESTANDO OS ENDPOINTS (DELETE "/prescricao/id") ----------------------------------------------------------------

    @Test
    void deveRetornar204EDeletarPrescricaoNoDelete() throws Exception {

        mockMvc.perform(delete("/prescricao/1"))
                .andExpect(status().isNoContent());

        verify(prescricaoService).deletarPrescricaoPeloId(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarPrescricaoNoDelete() throws Exception {

        doThrow(new ElementoNaoEncontradoException("Não foi encontrada prescrição com id 2"))
                .when(prescricaoService).deletarPrescricaoPeloId(2L);

        mockMvc.perform(delete("/prescricao/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    // TESTANDO OS ENDPOINTS (PUT "/prescricao/id") -------------------------------------------------------------------

    @Test
    void deveRetornar200EAtualizarPrescricaoNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "descricao": "Tratamento para hipertensão atualizado",
                    "medicamentos": "Medicamento B",
                    "idConsulta": 1,
                    "dataEmissao": "2030-05-20"
                }
                """;

        when(prescricaoService.atualizarPrescricao(anyLong(), any(PrescricaoRequestDto.class))).thenReturn(respostaFalsa());

        mockMvc.perform(put("/prescricao/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Tratamento para hipertensão"));

        verify(prescricaoService).atualizarPrescricao(anyLong(), any(PrescricaoRequestDto.class));

    }

    @Test
    void deveRetornar404AoNaoEncontrarPrescricaoNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "descricao": "Tratamento para hipertensão",
                    "idConsulta": 1
                }
                """;

        when(prescricaoService.atualizarPrescricao(anyLong(), any(PrescricaoRequestDto.class)))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrada prescrição com id 2"));

        mockMvc.perform(put("/prescricao/2").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPut() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (descrição em branco)
        String requestBodyDefeituoso = """
                {
                    "descricao": "",
                    "idConsulta": 1
                }
                """;

        mockMvc.perform(put("/prescricao/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(prescricaoService, never()).atualizarPrescricao(anyLong(), any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPut() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "descricao": "Tratamento para hipertensão
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(put("/prescricao/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(prescricaoService, never()).atualizarPrescricao(anyLong(), any());

    }

    @Test
    void deveRetornar409AoQuebrarRegraDeNegocioNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "descricao": "Tratamento para hipertensão",
                    "idConsulta": 1
                }
                """;

        when(prescricaoService.atualizarPrescricao(anyLong(), any(PrescricaoRequestDto.class)))
                .thenThrow(new RegraDeNegocioVioladaException("A prescrição não pôde ser salva pois a consulta com id 1 não está descrita como realizada."));

        mockMvc.perform(put("/prescricao/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(RegraDeNegocioVioladaException.class, result.getResolvedException()));

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private List<PrescricaoResponseDto> listaDeRespostasFalsas() {
        return List.of(
                respostaFalsa(),
                new PrescricaoResponseDto(2L, 2L, "Dra. Maria Silva", "654321-RJ", "Maria Silva", LocalDateTime.of(2030, 5, 21, 15, 0), "Tratamento para alergia", "Medicamento C", LocalDate.of(2030, 5, 21))
        );
    }

    private String jsonResponseDtoFalso() {
        return """
                {
                    "idConsulta": 1,
                    "nomeMedico": "Dr. Arthur Cosmo",
                    "crmMedico": "123456-SP",
                    "nomePaciente": "Arthur Cosmo",
                    "dataHoraConsulta": "2030-05-20T14:30:00",
                    "descricao": "Tratamento para hipertensão",
                    "medicamentos": "Medicamento A, 1 comprimido ao dia",
                    "dataEmissao": "20/05/2030"
                }
                """;
    }

    private PrescricaoResponseDto respostaFalsa() {
        return new PrescricaoResponseDto(1L, 1L, "Dr. Arthur Cosmo", "123456-SP", "Arthur Cosmo", LocalDateTime.of(2030, 5, 20, 14, 30), "Tratamento para hipertensão", "Medicamento A, 1 comprimido ao dia", LocalDate.of(2030, 5, 20));
    }

}
