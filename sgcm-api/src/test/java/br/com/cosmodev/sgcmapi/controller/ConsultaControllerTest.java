package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.ConsultaRequestDto;
import br.com.cosmodev.sgcmapi.dtos.ConsultaResponseDto;
import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.service.ConsultaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsultaController.class)
public class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaService consultaService;

    // TESTANDO OS ENDPOINTS (POST "/consulta") -----------------------------------------------------------------------

    @Test
    void deveRetornar201ESalvarAConsultaNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição
        String requestBody = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA",
                    "observacoes": "Consulta de rotina",
                    "idMedico": 1,
                    "idPaciente": 1
                }
                """;

        // PREPARAÇÃO: Mockando o service para retornar o ResponseDto genérico
        when(consultaService.salvarConsulta(any())).thenReturn(respostaFalsa());

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/consulta").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("AGENDADA"));

        // CONFIRMAÇÃO: Se o controller passou os dados corretos para o service
        verify(consultaService).salvarConsulta(any(ConsultaRequestDto.class));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (id do médico ausente)
        String requestBodyDefeituoso = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA",
                    "observacoes": "Consulta de rotina",
                    "idPaciente": 1
                }
                """;

        mockMvc.perform(post("/consulta").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(consultaService, never()).salvarConsulta(any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPost() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/consulta").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(consultaService, never()).salvarConsulta(any());

    }

    @Test
    void deveRetornar415QuandoContentTypeNaoForEnviadoNoPost() throws Exception {

        // EXECUÇÃO E CONFIRMAÇÃO: requisição sem contentType
        mockMvc.perform(post("/consulta").content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        // CONFIRMAÇÃO: nada chegou ao service
        verify(consultaService, never()).salvarConsulta(any());

    }

    @Test
    void deveRetornar409AoHaverConflitoDeHorarioNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição
        String requestBody = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA",
                    "observacoes": "Consulta de rotina",
                    "idMedico": 1,
                    "idPaciente": 1
                }
                """;

        // PREPARAÇÃO: service lança exception ao detectar sobreposição de horário
        when(consultaService.salvarConsulta(any(ConsultaRequestDto.class)))
                .thenThrow(new RegraDeNegocioVioladaException(
                        "O médico com id 1 já possui consulta de 20/05/30 14:20:00 até 20/05/30 14:40:00, o que conflita com o horário solicitado (20/05/30 14:30:00)."));

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/consulta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());

    }

    // TESTANDO OS ENDPOINTS (GET "/consulta") ------------------------------------------------------------------------

    @Test
    void deveRetornar200EListarConsultasNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista com todas as consultas
        when(consultaService.listarConsultas()).thenReturn(listaDeRespostasFalsas());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/consulta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nomeMedico").value("Dr. Arthur Cosmo"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nomePaciente").value("Maria Silva"));

        verify(consultaService).listarConsultas();

    }

    @Test
    void deveRetornar200EListaVaziaNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista vazia de consultas
        when(consultaService.listarConsultas()).thenReturn(Collections.emptyList());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/consulta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(consultaService).listarConsultas();

    }

    // TESTANDO OS ENDPOINTS (GET "/consulta/id") ---------------------------------------------------------------------

    @Test
    void deveRetornar200EConsultaPorIdNoGetId() throws Exception {

        // PREPARAÇÃO
        when(consultaService.buscarConsultaPorId(anyLong())).thenReturn(respostaFalsa());

        mockMvc.perform(get("/consulta/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponseDtoFalso()));

        verify(consultaService).buscarConsultaPorId(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarConsultaNoGetId() throws Exception {

        when(consultaService.buscarConsultaPorId(2L))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrada consulta com id 2"));

        mockMvc.perform(get("/consulta/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

        verify(consultaService).buscarConsultaPorId(2L);

    }

    @Test
    void deveRetornar400SeOIdNaoForNumericoNoGetId() throws Exception {

        mockMvc.perform(get("/consulta/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentTypeMismatchException.class, result.getResolvedException()));

        verify(consultaService, never()).buscarConsultaPorId(any());

    }

    // TESTANDO OS ENDPOINTS (DELETE "/consulta/id") ------------------------------------------------------------------

    @Test
    void deveRetornar204EDeletarConsultaNoDelete() throws Exception {

        mockMvc.perform(delete("/consulta/1"))
                .andExpect(status().isNoContent());

        verify(consultaService).deletarConsultaPorId(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarConsultaNoDelete() throws Exception {

        doThrow(new ElementoNaoEncontradoException("Não foi encontrada consulta com id 2"))
                .when(consultaService).deletarConsultaPorId(2L);

        mockMvc.perform(delete("/consulta/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    // TESTANDO OS ENDPOINTS (PUT "/consulta/id") ---------------------------------------------------------------------

    @Test
    void deveRetornar200EAtualizarConsultaNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA",
                    "observacoes": "Consulta de rotina",
                    "idMedico": 1,
                    "idPaciente": 1
                }
                """;

        when(consultaService.atualizarConsulta(anyLong(), any(ConsultaRequestDto.class))).thenReturn(respostaFalsa());

        mockMvc.perform(put("/consulta/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("AGENDADA"));

        verify(consultaService).atualizarConsulta(anyLong(), any(ConsultaRequestDto.class));

    }

    @Test
    void deveRetornar404AoNaoEncontrarConsultaNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA",
                    "idMedico": 1,
                    "idPaciente": 1
                }
                """;

        when(consultaService.atualizarConsulta(anyLong(), any(ConsultaRequestDto.class)))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrada consulta com id 2"));

        mockMvc.perform(put("/consulta/2").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPut() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (id do médico ausente)
        String requestBodyDefeituoso = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA",
                    "idPaciente": 1
                }
                """;

        mockMvc.perform(put("/consulta/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(consultaService, never()).atualizarConsulta(anyLong(), any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPut() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(put("/consulta/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(consultaService, never()).atualizarConsulta(anyLong(), any());

    }

    @Test
    void deveRetornar409AoHaverConflitoDeHorarioNoPut() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição
        String requestBody = """
                {
                    "dataHora": "2030-05-20T14:30:00",
                    "status": "AGENDADA",
                    "observacoes": "Consulta de rotina",
                    "idMedico": 1,
                    "idPaciente": 1
                }
                """;

        // PREPARAÇÃO: service lança exception ao detectar sobreposição de horário
        when(consultaService.atualizarConsulta(eq(1L), any(ConsultaRequestDto.class)))
                .thenThrow(new RegraDeNegocioVioladaException(
                        "O médico com id 1 já possui consulta de 20/05/30 14:20:00 até 20/05/30 14:40:00, o que conflita com o horário solicitado (20/05/30 14:30:00)."));

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(put("/consulta/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private List<ConsultaResponseDto> listaDeRespostasFalsas() {
        return List.of(
                respostaFalsa(),
                new ConsultaResponseDto(2L, LocalDateTime.of(2030, 5, 21, 15, 0), StatusConsulta.CONFIRMADA, "Retorno", 2L, "Dra. Maria Silva", "654321-RJ", 2L, "Maria Silva")
        );
    }

    private String jsonResponseDtoFalso() {
        return """
                {
                    "dataHora": "20/05/2030 14:30:00",
                    "status": "AGENDADA",
                    "observacoes": "Consulta de rotina",
                    "idMedico": 1,
                    "nomeMedico": "Dr. Arthur Cosmo",
                    "crmMedico": "123456-SP",
                    "idPaciente": 1,
                    "nomePaciente": "Arthur Cosmo"
                }
                """;
    }

    private ConsultaResponseDto respostaFalsa() {
        return new ConsultaResponseDto(1L, LocalDateTime.of(2030, 5, 20, 14, 30), StatusConsulta.AGENDADA, "Consulta de rotina", 1L, "Dr. Arthur Cosmo", "123456-SP", 1L, "Arthur Cosmo");
    }

}
