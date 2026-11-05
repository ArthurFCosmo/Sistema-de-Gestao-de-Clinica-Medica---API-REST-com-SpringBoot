package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.PacienteRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PacienteResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.service.PacienteService;
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

@WebMvcTest(PacienteController.class)
public class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    // TESTANDO OS ENDPOINTS (POST "/paciente") -----------------------------------------------------------------------

    @Test
    void deveRetornar201ESalvarOPacienteNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição
        String requestBody = """
                {
                    "nome": "Arthur Cosmo",
                    "cpf": "12345678909",
                    "email": "arthur@email.com",
                    "telefone": "(11) 99999-9999",
                    "dataNascimento": "20/05/1990"
                }
                """;

        // PREPARAÇÃO: Mockando o service para retornar o ResponseDto genérico
        when(pacienteService.salvarPaciente(any())).thenReturn(respostaFalsa());

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/paciente").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Arthur Cosmo"));

        // CONFIRMAÇÃO: Se o controller passou os dados corretos para o service
        verify(pacienteService).salvarPaciente(any(PacienteRequestDto.class));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (nome em branco)
        String requestBodyDefeituoso = """
                {
                    "nome": "",
                    "cpf": "12345678909",
                    "email": "arthur@email.com",
                    "telefone": "(11) 99999-9999",
                    "dataNascimento": "20/05/1990"
                }
                """;

        mockMvc.perform(post("/paciente").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(pacienteService, never()).salvarPaciente(any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPost() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "nome": "Arthur Cosmo",
                    "cpf": "12345678909
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/paciente").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(pacienteService, never()).salvarPaciente(any());

    }

    @Test
    void deveRetornar415QuandoContentTypeNaoForEnviadoNoPost() throws Exception {

        // EXECUÇÃO E CONFIRMAÇÃO: requisição sem contentType
        mockMvc.perform(post("/paciente").content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        // CONFIRMAÇÃO: nada chegou ao service
        verify(pacienteService, never()).salvarPaciente(any());

    }

    // TESTANDO OS ENDPOINTS (GET "/paciente") ------------------------------------------------------------------------

    @Test
    void deveRetornar200EListarPacientesNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista com todos os pacientes
        when(pacienteService.listarPacientes()).thenReturn(listaDeRespostasFalsas());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/paciente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Arthur Cosmo"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Maria Silva"));

        verify(pacienteService).listarPacientes();

    }

    @Test
    void deveRetornar200EListaVaziaNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista vazia de pacientes
        when(pacienteService.listarPacientes()).thenReturn(Collections.emptyList());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/paciente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(pacienteService).listarPacientes();

    }

    // TESTANDO OS ENDPOINTS (GET "/paciente/id") ---------------------------------------------------------------------

    @Test
    void deveRetornar200EPacientePorIdNoGetId() throws Exception {

        // PREPARAÇÃO
        when(pacienteService.buscarPacientePorId(anyLong())).thenReturn(respostaFalsa());

        mockMvc.perform(get("/paciente/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponseDtoFalso()));

        verify(pacienteService).buscarPacientePorId(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarPacienteNoGetId() throws Exception {

        when(pacienteService.buscarPacientePorId(2L))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrado paciente com o id 2"));

        mockMvc.perform(get("/paciente/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

        verify(pacienteService).buscarPacientePorId(2L);

    }

    @Test
    void deveRetornar400SeOIdNaoForNumericoNoGetId() throws Exception {

        mockMvc.perform(get("/paciente/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentTypeMismatchException.class, result.getResolvedException()));

        verify(pacienteService, never()).buscarPacientePorId(any());

    }

    // TESTANDO OS ENDPOINTS (DELETE "/paciente/id") ------------------------------------------------------------------

    @Test
    void deveRetornar204EDeletarPacienteNoDelete() throws Exception {

        mockMvc.perform(delete("/paciente/1"))
                .andExpect(status().isNoContent());

        verify(pacienteService).deletarPaciente(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarPacienteNoDelete() throws Exception {

        doThrow(new ElementoNaoEncontradoException("Não foi encontrado paciente com o id 2"))
                .when(pacienteService).deletarPaciente(2L);

        mockMvc.perform(delete("/paciente/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    @Test
    void deveRetornar409AoQuebrarRegraDeNegocioNoDelete() throws Exception {

        doThrow(new RegraDeNegocioVioladaException("O paciente com id 2 não pôde ser deletado, pois possui consultas ativas."))
                .when(pacienteService).deletarPaciente(2L);

        mockMvc.perform(delete("/paciente/2"))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(RegraDeNegocioVioladaException.class, result.getResolvedException()));

    }

    // TESTANDO OS ENDPOINTS (PUT "/paciente/id") ---------------------------------------------------------------------

    @Test
    void deveRetornar200EAtualizarPacienteNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "nome": "Arthur Cosmo",
                    "cpf": "12345678909",
                    "email": "arthur@email.com",
                    "telefone": "(11) 99999-9999",
                    "dataNascimento": "20/05/1990",
                    "ativo": true
                }
                """;

        when(pacienteService.atualizarPaciente(anyLong(), any(PacienteRequestDto.class))).thenReturn(respostaFalsa());

        mockMvc.perform(put("/paciente/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Arthur Cosmo"));

        verify(pacienteService).atualizarPaciente(anyLong(), any(PacienteRequestDto.class));

    }

    @Test
    void deveRetornar404AoNaoEncontrarPacienteNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "nome": "Arthur Cosmo",
                    "cpf": "12345678909",
                    "email": "arthur@email.com",
                    "telefone": "(11) 99999-9999",
                    "dataNascimento": "20/05/1990"
                }
                """;

        when(pacienteService.atualizarPaciente(anyLong(), any(PacienteRequestDto.class)))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrado paciente com o id 2"));

        mockMvc.perform(put("/paciente/2").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPut() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (nome em branco)
        String requestBodyDefeituoso = """
                {
                    "nome": "",
                    "cpf": "12345678909",
                    "email": "arthur@email.com",
                    "telefone": "(11) 99999-9999",
                    "dataNascimento": "20/05/1990"
                }
                """;

        mockMvc.perform(put("/paciente/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(pacienteService, never()).atualizarPaciente(anyLong(), any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPut() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "nome": "Arthur Cosmo",
                    "cpf": "12345678909
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(put("/paciente/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(pacienteService, never()).atualizarPaciente(anyLong(), any());

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private List<PacienteResponseDto> listaDeRespostasFalsas() {
        return List.of(
                respostaFalsa(),
                new PacienteResponseDto(2L, "Maria Silva", "987.***.***-**", "maria@email.com", "(21) 98888-8888", 35, true)
        );
    }

    private String jsonResponseDtoFalso() {
        return """
                {
                    "nome": "Arthur Cosmo",
                    "cpfMascarado": "123.***.***-**",
                    "email": "arthur@email.com",
                    "telefone": "(11) 99999-9999",
                    "idade": 36,
                    "ativo": true
                }
                """;
    }

    private PacienteResponseDto respostaFalsa() {
        return new PacienteResponseDto(1L, "Arthur Cosmo", "123.***.***-**", "arthur@email.com", "(11) 99999-9999", 36, true);
    }

}
