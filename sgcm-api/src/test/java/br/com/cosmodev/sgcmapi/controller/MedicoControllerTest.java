package br.com.cosmodev.sgcmapi.controller;

import br.com.cosmodev.sgcmapi.dtos.MedicoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.MedicoResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.service.MedicoService;
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

@WebMvcTest(MedicoController.class)
public class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicoService medicoService;

    // TESTANDO OS ENDPOINTS (POST "/medico") -------------------------------------------------------------------------

    @Test
    void deveRetornar201ESalvarOMedicoNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição
        String requestBody = """
                {
                    "nome": "Dr. Arthur Cosmo",
                    "crm": "123456-SP",
                    "email": "arthur.medico@email.com",
                    "telefone": "(11) 99999-9999",
                    "idEspecialidade": 1
                }
                """;

        // PREPARAÇÃO: Mockando o service para retornar o ResponseDto genérico
        when(medicoService.salvarMedico(any())).thenReturn(respostaFalsa());

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/medico").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Dr. Arthur Cosmo"));

        // CONFIRMAÇÃO: Se o controller passou os dados corretos para o service
        verify(medicoService).salvarMedico(any(MedicoRequestDto.class));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPost() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (nome em branco)
        String requestBodyDefeituoso = """
                {
                    "nome": "",
                    "crm": "123456-SP",
                    "email": "arthur.medico@email.com",
                    "telefone": "(11) 99999-9999",
                    "idEspecialidade": 1
                }
                """;

        mockMvc.perform(post("/medico").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(medicoService, never()).salvarMedico(any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPost() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "nome": "Dr. Arthur Cosmo",
                    "crm": "123456-SP
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(post("/medico").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(medicoService, never()).salvarMedico(any());

    }

    @Test
    void deveRetornar415QuandoContentTypeNaoForEnviadoNoPost() throws Exception {

        // EXECUÇÃO E CONFIRMAÇÃO: requisição sem contentType
        mockMvc.perform(post("/medico").content("{}"))
                .andExpect(status().isUnsupportedMediaType());

        // CONFIRMAÇÃO: nada chegou ao service
        verify(medicoService, never()).salvarMedico(any());

    }

    // TESTANDO OS ENDPOINTS (GET "/medico") --------------------------------------------------------------------------

    @Test
    void deveRetornar200EListarMedicosNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista com todos os médicos
        when(medicoService.listarMedicos()).thenReturn(listaDeRespostasFalsas());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/medico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Dr. Arthur Cosmo"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Dra. Maria Silva"));

        verify(medicoService).listarMedicos();

    }

    @Test
    void deveRetornar200EListaVaziaNoGet() throws Exception {

        // PREPARAÇÃO: Retornando lista vazia de médicos
        when(medicoService.listarMedicos()).thenReturn(Collections.emptyList());

        // CONFIRMAÇÃO
        mockMvc.perform(get("/medico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(medicoService).listarMedicos();

    }

    // TESTANDO OS ENDPOINTS (GET "/medico/id") -----------------------------------------------------------------------

    @Test
    void deveRetornar200EMedicoPorIdNoGetId() throws Exception {

        // PREPARAÇÃO
        when(medicoService.buscarMedicoPorId(anyLong())).thenReturn(respostaFalsa());

        mockMvc.perform(get("/medico/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponseDtoFalso()));

        verify(medicoService).buscarMedicoPorId(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarMedicoNoGetId() throws Exception {

        when(medicoService.buscarMedicoPorId(2L))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrado médico com o id 2"));

        mockMvc.perform(get("/medico/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

        verify(medicoService).buscarMedicoPorId(2L);

    }

    @Test
    void deveRetornar400SeOIdNaoForNumericoNoGetId() throws Exception {

        mockMvc.perform(get("/medico/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentTypeMismatchException.class, result.getResolvedException()));

        verify(medicoService, never()).buscarMedicoPorId(any());

    }

    // TESTANDO OS ENDPOINTS (DELETE "/medico/id") --------------------------------------------------------------------

    @Test
    void deveRetornar204EDeletarMedicoNoDelete() throws Exception {

        mockMvc.perform(delete("/medico/1"))
                .andExpect(status().isNoContent());

        verify(medicoService).deletarMedico(1L);

    }

    @Test
    void deveRetornar404AoNaoEncontrarMedicoNoDelete() throws Exception {

        doThrow(new ElementoNaoEncontradoException("Não foi encontrado médico com o id 2"))
                .when(medicoService).deletarMedico(2L);

        mockMvc.perform(delete("/medico/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    @Test
    void deveRetornar409AoQuebrarRegraDeNegocioNoDelete() throws Exception {

        doThrow(new RegraDeNegocioVioladaException("O medico com id 2 não pôde ser deletado, pois possui consultas ativas."))
                .when(medicoService).deletarMedico(2L);

        mockMvc.perform(delete("/medico/2"))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(RegraDeNegocioVioladaException.class, result.getResolvedException()));

    }

    // TESTANDO OS ENDPOINTS (PUT "/medico/id") -----------------------------------------------------------------------

    @Test
    void deveRetornar200EAtualizarMedicoNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "nome": "Dr. Arthur Cosmo",
                    "crm": "123456-SP",
                    "email": "arthur.medico@email.com",
                    "telefone": "(11) 99999-9999",
                    "idEspecialidade": 1,
                    "ativo": true
                }
                """;

        when(medicoService.atualizarMedico(anyLong(), any(MedicoRequestDto.class))).thenReturn(respostaFalsa());

        mockMvc.perform(put("/medico/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Dr. Arthur Cosmo"));

        verify(medicoService).atualizarMedico(anyLong(), any(MedicoRequestDto.class));

    }

    @Test
    void deveRetornar404AoNaoEncontrarMedicoNoPut() throws Exception {

        // PREPARAÇÃO: Criando o requestbody em JSON
        String requestBody = """
                {
                    "nome": "Dr. Arthur Cosmo",
                    "crm": "123456-SP",
                    "email": "arthur.medico@email.com",
                    "telefone": "(11) 99999-9999",
                    "idEspecialidade": 1
                }
                """;

        when(medicoService.atualizarMedico(anyLong(), any(MedicoRequestDto.class)))
                .thenThrow(new ElementoNaoEncontradoException("Não foi encontrado médico com o id 2"));

        mockMvc.perform(put("/medico/2").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ElementoNaoEncontradoException.class, result.getResolvedException()));

    }

    @Test
    void deveRetornar400QuandoOBodyEstiverInvalidoNoPut() throws Exception {

        // PREPARAÇÃO: JSON enviado no body da requisição (nome em branco)
        String requestBodyDefeituoso = """
                {
                    "nome": "",
                    "crm": "123456-SP",
                    "email": "arthur.medico@email.com",
                    "telefone": "(11) 99999-9999",
                    "idEspecialidade": 1
                }
                """;

        mockMvc.perform(put("/medico/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));

        verify(medicoService, never()).atualizarMedico(anyLong(), any());

    }

    @Test
    void deveRetornar400QuandoJsonEstiverMalformadoNoPut() throws Exception {

        // PREPARAÇÃO: JSON sintaticamente inválido (falta fechar a chave e as aspas)
        String requestBodyDefeituoso = """
                {
                    "nome": "Dr. Arthur Cosmo",
                    "crm": "123456-SP
                """;

        // EXECUÇÃO E CONFIRMAÇÃO
        mockMvc.perform(put("/medico/1").contentType(MediaType.APPLICATION_JSON).content(requestBodyDefeituoso))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(HttpMessageNotReadableException.class, result.getResolvedException()));

        // CONFIRMAÇÃO: nada chegou ao service
        verify(medicoService, never()).atualizarMedico(anyLong(), any());

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private List<MedicoResponseDto> listaDeRespostasFalsas() {
        return List.of(
                respostaFalsa(),
                new MedicoResponseDto(2L, "Dra. Maria Silva", "654321-RJ", "maria.medico@email.com", "(21) 98888-8888", true, "Oncologista")
        );
    }

    private String jsonResponseDtoFalso() {
        return """
                {
                    "nome": "Dr. Arthur Cosmo",
                    "crm": "123456-SP",
                    "email": "arthur.medico@email.com",
                    "telefone": "(11) 99999-9999",
                    "ativo": true,
                    "especialidade": "Cardiologista"
                }
                """;
    }

    private MedicoResponseDto respostaFalsa() {
        return new MedicoResponseDto(1L, "Dr. Arthur Cosmo", "123456-SP", "arthur.medico@email.com", "(11) 99999-9999", true, "Cardiologista");
    }

}
