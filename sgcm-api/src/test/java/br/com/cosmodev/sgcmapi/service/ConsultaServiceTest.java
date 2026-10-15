package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.ConsultaRequestDto;
import br.com.cosmodev.sgcmapi.dtos.ConsultaResponseDto;
import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.model.Consulta;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.cosmodev.sgcmapi.service.UtilsSgcm.FORMATO_DATA_HORA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaServiceTest {

    private static final LocalDateTime DATA_HORA_BASE = LocalDateTime.of(2030, 5, 10, 14, 30);
    private static final long DURACAO_EM_MINUTOS = 20L;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private ConsultaService consultaService;

    // TESTANDO MÉTODOS DE CONVERSÃO DE ENTIDADES ----------------------------------------------------------------------

    @Test
    void deveConverterDtoParaModelComSucesso() {
        ConsultaRequestDto dto = consultaDto();
        Medico medico = medicoModelo();
        Paciente paciente = pacienteModelo();
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));

        Consulta resultado = consultaService.converterParaModel(dto);

        assertAll(
                () -> assertNull(resultado.getId()),
                () -> assertEquals(dto.dataHora(), resultado.getDataHora()),
                () -> assertEquals(dto.status(), resultado.getStatus()),
                () -> assertEquals(dto.observacoes(), resultado.getObservacoes()),
                () -> assertEquals(medico, resultado.getMedico()),
                () -> assertEquals(paciente, resultado.getPaciente())
        );
        verify(medicoRepository).findById(1L);
        verify(pacienteRepository).findById(2L);
    }

    @Test
    void deveConverterDtoParaModelComIdComSucesso() {
        ConsultaRequestDto dto = consultaDto();
        Medico medico = medicoModelo();
        Paciente paciente = pacienteModelo();
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));

        Consulta resultado = consultaService.converterParaModel(7L, dto);

        assertAll(
                () -> assertEquals(7L, resultado.getId()),
                () -> assertEquals(dto.dataHora(), resultado.getDataHora()),
                () -> assertEquals(dto.status(), resultado.getStatus()),
                () -> assertEquals(dto.observacoes(), resultado.getObservacoes()),
                () -> assertEquals(medico, resultado.getMedico()),
                () -> assertEquals(paciente, resultado.getPaciente())
        );
        verify(medicoRepository).findById(1L);
        verify(pacienteRepository).findById(2L);
    }

    @Test
    void deveConverterModelParaDtoComSucesso() {
        Consulta consulta = new Consulta(
                3L,
                LocalDateTime.of(2030, 5, 10, 14, 30),
                StatusConsulta.AGENDADA,
                "Consulta de rotina",
                medicoModelo(),
                pacienteModelo()
        );

        ConsultaResponseDto resultado = consultaService.converterParaResponseDto(consulta);

        assertAll(
                () -> assertEquals(3L, resultado.id()),
                () -> assertEquals(consulta.getDataHora(), resultado.dataHora()),
                () -> assertEquals(consulta.getStatus(), resultado.status()),
                () -> assertEquals("Consulta de rotina", resultado.observacoes()),
                () -> assertEquals(1L, resultado.idMedico()),
                () -> assertEquals("Arthur Cosmo", resultado.nomeMedico()),
                () -> assertEquals("123456-PE", resultado.crmMedico()),
                () -> assertEquals(2L, resultado.idPaciente()),
                () -> assertEquals("Ana Silva", resultado.nomePaciente())
        );
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreMedicoParaConversao() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> consultaService.converterParaModel(consultaDto())
        );

        assertEquals("Não foi encontrado médico com o id 1", exception.getMessage());
        verify(pacienteRepository, never()).findById(any());
    }

    @Test
    void deveLancarExceptionCasoNaoEncontrePacienteParaConversao() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoModelo()));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> consultaService.converterParaModel(consultaDto())
        );

        assertEquals("Não foi encontrado paciente com o id 2", exception.getMessage());
        verify(medicoRepository).findById(1L);
        verify(pacienteRepository).findById(2L);
    }

    // TESTANDO MÉTODOS PADRÃO DO CRUD ---------------------------------------------------------------------------------

    @Test
    void deveSalvarConsultaComSucesso() {
        ConsultaRequestDto dto = consultaDto();
        mockarSalvamento(3L, dto);

        ConsultaResponseDto resultado = consultaService.salvarConsulta(dto);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(3L, resultado.id()),
                () -> assertEquals(dto.dataHora(), resultado.dataHora()),
                () -> assertEquals(dto.status(), resultado.status()),
                () -> assertEquals(dto.observacoes(), resultado.observacoes()),
                () -> assertEquals(1L, resultado.idMedico()),
                () -> assertEquals("Arthur Cosmo", resultado.nomeMedico()),
                () -> assertEquals("123456-PE", resultado.crmMedico()),
                () -> assertEquals(2L, resultado.idPaciente()),
                () -> assertEquals("Ana Silva", resultado.nomePaciente())
        );
        verify(medicoRepository).findById(1L);
        verify(pacienteRepository).findById(2L);
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveBuscarConsultaPorIdComSucesso() {
        Consulta consulta = consultaModelo(3L, consultaDto());
        when(consultaRepository.findById(3L)).thenReturn(Optional.of(consulta));

        ConsultaResponseDto resultado = consultaService.buscarConsultaPorId(3L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(3L, resultado.id()),
                () -> assertEquals("Consulta de rotina", resultado.observacoes()),
                () -> assertEquals("Arthur Cosmo", resultado.nomeMedico()),
                () -> assertEquals("Ana Silva", resultado.nomePaciente())
        );
        verify(consultaRepository).findById(3L);
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreConsultaPorId() {
        when(consultaRepository.findById(999L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> consultaService.buscarConsultaPorId(999L)
        );

        assertEquals("Não foi encontrada consulta com id 999", exception.getMessage());
        verify(consultaRepository).findById(999L);
    }

    @Test
    void deveListarTodasConsultasComSucesso() {
        List<Consulta> consultas = List.of(
                consultaModelo(3L, consultaDto()),
                consultaModelo(4L, new ConsultaRequestDto(
                        LocalDateTime.of(2030, 5, 11, 15, 0),
                        StatusConsulta.CONFIRMADA,
                        "Retorno",
                        1L,
                        2L
                ))
        );
        when(consultaRepository.findAll()).thenReturn(consultas);

        List<ConsultaResponseDto> resultado = consultaService.listarConsultas();

        assertAll(
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals(3L, resultado.get(0).id()),
                () -> assertEquals("Consulta de rotina", resultado.get(0).observacoes()),
                () -> assertEquals(4L, resultado.get(1).id()),
                () -> assertEquals("Retorno", resultado.get(1).observacoes()),
                () -> assertEquals(StatusConsulta.CONFIRMADA, resultado.get(1).status())
        );
        verify(consultaRepository).findAll();
    }

    @Test
    void deveListarZeroConsultas() {
        when(consultaRepository.findAll()).thenReturn(List.of());

        List<ConsultaResponseDto> resultado = consultaService.listarConsultas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(consultaRepository).findAll();
    }

    @Test
    void deveDeletarConsultaComSucessoQuandoExistir() {
        when(consultaRepository.existsById(3L)).thenReturn(true);

        consultaService.deletarConsultaPorId(3L);

        verify(consultaRepository).existsById(3L);
        verify(consultaRepository).deleteById(3L);
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreConsultaParaDeletar() {
        when(consultaRepository.existsById(999L)).thenReturn(false);

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> consultaService.deletarConsultaPorId(999L)
        );

        assertEquals("Não foi encontrada consulta com id 999", exception.getMessage());
        verify(consultaRepository, never()).deleteById(any());
    }

    @Test
    void deveAtualizarConsultaComSucesso() {
        ConsultaRequestDto dtoAtualizado = new ConsultaRequestDto(
                LocalDateTime.of(2030, 5, 12, 16, 0),
                StatusConsulta.CONFIRMADA,
                "Consulta atualizada",
                1L,
                2L
        );
        when(consultaRepository.existsById(3L)).thenReturn(true);
        mockarSalvamento(3L, dtoAtualizado);

        ConsultaResponseDto resultado = consultaService.atualizarConsulta(3L, dtoAtualizado);

        assertAll(
                () -> assertEquals(3L, resultado.id()),
                () -> assertEquals(dtoAtualizado.dataHora(), resultado.dataHora()),
                () -> assertEquals(dtoAtualizado.status(), resultado.status()),
                () -> assertEquals("Consulta atualizada", resultado.observacoes()),
                () -> assertEquals(1L, resultado.idMedico()),
                () -> assertEquals(2L, resultado.idPaciente())
        );
        verify(consultaRepository).existsById(3L);
        verify(medicoRepository).findById(1L);
        verify(pacienteRepository).findById(2L);
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreConsultaParaAtualizar() {
        when(consultaRepository.existsById(999L)).thenReturn(false);

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> consultaService.atualizarConsulta(999L, consultaDto())
        );

        assertEquals("Não foi encontrada consulta com id 999", exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
        verify(consultaRepository, never()).findByMedico_IdAndDataHoraBetween(any(), any(), any());
    }

    // TESTANDO REGRA: CONFLITO DE HORÁRIO DO MÉDICO (via salvarConsulta) ----------------------------------------------

    @Test
    void deveConsultarCandidatasNaJanelaCorretaAoSalvar() {
        ConsultaRequestDto dto = consultaDto();
        mockarCandidatas(); // sem candidatas
        mockarSalvamento(3L, dto);

        consultaService.salvarConsulta(dto);

        // Janela: [inicio - 20min, fim da nova consulta (inicio + 20min)]
        verify(consultaRepository).findByMedico_IdAndDataHoraBetween(
                1L,
                DATA_HORA_BASE.minusMinutes(DURACAO_EM_MINUTOS),
                DATA_HORA_BASE.plusMinutes(DURACAO_EM_MINUTOS)
        );

    }

    // Offsets (em minutos) que SOBREPÕEM a nova consulta de 20 min: qualquer diferença de -19 a +19
    @ParameterizedTest(name = "existente com {0} min de diferença deve gerar conflito")
    @ValueSource(ints = {-19, -10, 0, 10, 19})
    void deveLancarExceptionAoSalvarQuandoHorarioSobrepoeConsultaAtivaDoMedico(int diferencaEmMinutos) {
        ConsultaRequestDto dto = consultaDto();
        LocalDateTime inicioExistente = DATA_HORA_BASE.plusMinutes(diferencaEmMinutos);
        mockarCandidatas(consultaExistente(10L, inicioExistente, StatusConsulta.AGENDADA));

        RegraDeNegocioVioladaException exception = assertThrows(
                RegraDeNegocioVioladaException.class,
                () -> consultaService.salvarConsulta(dto)
        );

        assertEquals(mensagemDeConflito(1L, inicioExistente, DATA_HORA_BASE), exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
        verify(medicoRepository, never()).findById(any());
        verify(pacienteRepository, never()).findById(any());
    }

    // Offsets que apenas "encostam" na nova consulta: o fim de uma é o início da outra, então está livre
    @ParameterizedTest(name = "existente com {0} min de diferença deve estar livre")
    @ValueSource(ints = {-20, 20})
    void deveSalvarQuandoConsultaExistenteApenasEncostaNoHorario(int diferencaEmMinutos) {
        ConsultaRequestDto dto = consultaDto();
        mockarCandidatas(consultaExistente(10L, DATA_HORA_BASE.plusMinutes(diferencaEmMinutos), StatusConsulta.AGENDADA));
        mockarSalvamento(3L, dto);

        ConsultaResponseDto resultado = consultaService.salvarConsulta(dto);

        assertNotNull(resultado);
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveSalvarQuandoConsultaExistenteNoMesmoHorarioEstaCancelada() {
        ConsultaRequestDto dto = consultaDto();
        mockarCandidatas(consultaExistente(10L, DATA_HORA_BASE, StatusConsulta.CANCELADA));
        mockarSalvamento(3L, dto);

        ConsultaResponseDto resultado = consultaService.salvarConsulta(dto);

        assertNotNull(resultado);
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveLancarExceptionMesmoQuandoConsultaConflitanteEstaConfirmada() {
        mockarCandidatas(consultaExistente(10L, DATA_HORA_BASE, StatusConsulta.CONFIRMADA));

        assertThrows(RegraDeNegocioVioladaException.class, () -> consultaService.salvarConsulta(consultaDto()));

        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    void deveLancarExceptionQuandoHaConsultaCanceladaEOutraConflitanteNaLista() {
        // Garante que a cancelada é ignorada (continue) e a verificação segue para as próximas
        mockarCandidatas(
                consultaExistente(10L, DATA_HORA_BASE, StatusConsulta.CANCELADA),
                consultaExistente(11L, DATA_HORA_BASE.plusMinutes(10), StatusConsulta.AGENDADA)
        );

        RegraDeNegocioVioladaException exception = assertThrows(
                RegraDeNegocioVioladaException.class,
                () -> consultaService.salvarConsulta(consultaDto())
        );

        assertEquals(mensagemDeConflito(1L, DATA_HORA_BASE.plusMinutes(10), DATA_HORA_BASE), exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    void deveIgnorarVerificacaoDeConflitoAoSalvarConsultaComStatusCancelada() {
        ConsultaRequestDto dtoCancelada = new ConsultaRequestDto(
                DATA_HORA_BASE, StatusConsulta.CANCELADA, "Cancelada", 1L, 2L
        );
        mockarSalvamento(3L, dtoCancelada);

        ConsultaResponseDto resultado = consultaService.salvarConsulta(dtoCancelada);

        assertNotNull(resultado);
        verify(consultaRepository, never()).findByMedico_IdAndDataHoraBetween(any(), any(), any());
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveIgnorarVerificacaoDeConflitoAoSalvarConsultaComStatusRealizada() {
        ConsultaRequestDto dtoRealizada = new ConsultaRequestDto(
                DATA_HORA_BASE, StatusConsulta.REALIZADA, "Realizada", 1L, 2L
        );
        mockarSalvamento(3L, dtoRealizada);

        ConsultaResponseDto resultado = consultaService.salvarConsulta(dtoRealizada);

        assertNotNull(resultado);
        verify(consultaRepository, never()).findByMedico_IdAndDataHoraBetween(any(), any(), any());
        verify(consultaRepository).save(any(Consulta.class));
    }

    // TESTANDO REGRA: CONFLITO DE HORÁRIO DO MÉDICO (via atualizarConsulta) -------------------------------------------

    @Test
    void deveAtualizarSemConflitoQuandoUnicaConsultaNoHorarioEhAPropriaConsulta() {
        ConsultaRequestDto dto = consultaDto();
        when(consultaRepository.existsById(3L)).thenReturn(true);
        mockarCandidatas(consultaExistente(3L, DATA_HORA_BASE, StatusConsulta.AGENDADA));
        mockarSalvamento(3L, dto);

        ConsultaResponseDto resultado = consultaService.atualizarConsulta(3L, dto);

        assertEquals(3L, resultado.id());
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveLancarExceptionAoAtualizarQuandoOutraConsultaConflita() {
        ConsultaRequestDto dto = consultaDto();
        when(consultaRepository.existsById(3L)).thenReturn(true);
        mockarCandidatas(
                consultaExistente(3L, DATA_HORA_BASE, StatusConsulta.AGENDADA), // a própria, ignorada
                consultaExistente(8L, DATA_HORA_BASE.plusMinutes(5), StatusConsulta.AGENDADA) // outra, conflita
        );

        RegraDeNegocioVioladaException exception = assertThrows(
                RegraDeNegocioVioladaException.class,
                () -> consultaService.atualizarConsulta(3L, dto)
        );

        assertEquals(mensagemDeConflito(1L, DATA_HORA_BASE.plusMinutes(5), DATA_HORA_BASE), exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    void deveIgnorarVerificacaoDeConflitoAoAtualizarConsultaParaStatusCancelada() {
        ConsultaRequestDto dtoCancelada = new ConsultaRequestDto(
                DATA_HORA_BASE, StatusConsulta.CANCELADA, "Cancelada", 1L, 2L
        );
        when(consultaRepository.existsById(3L)).thenReturn(true);
        mockarSalvamento(3L, dtoCancelada);

        ConsultaResponseDto resultado = consultaService.atualizarConsulta(3L, dtoCancelada);

        assertEquals(StatusConsulta.CANCELADA, resultado.status());
        verify(consultaRepository, never()).findByMedico_IdAndDataHoraBetween(any(), any(), any());
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveIgnorarVerificacaoDeConflitoAoAtualizarConsultaParaStatusRealizada() {
        ConsultaRequestDto dtoRealizada = new ConsultaRequestDto(
                DATA_HORA_BASE, StatusConsulta.REALIZADA, "Realizada", 1L, 2L
        );
        when(consultaRepository.existsById(3L)).thenReturn(true);
        mockarSalvamento(3L, dtoRealizada);

        ConsultaResponseDto resultado = consultaService.atualizarConsulta(3L, dtoRealizada);

        assertEquals(StatusConsulta.REALIZADA, resultado.status());
        verify(consultaRepository, never()).findByMedico_IdAndDataHoraBetween(any(), any(), any());
        verify(consultaRepository).save(any(Consulta.class));
    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private void mockarCandidatas(Consulta... candidatas) {
        when(consultaRepository.findByMedico_IdAndDataHoraBetween(eq(1L), any(), any()))
                .thenReturn(List.of(candidatas));
    }

    private void mockarSalvamento(Long idSalvo, ConsultaRequestDto dto) {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoModelo()));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(pacienteModelo()));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consultaModelo(idSalvo, dto));
    }

    private String mensagemDeConflito(Long idMedico, LocalDateTime inicioExistente, LocalDateTime inicioSolicitado) {
        return "O médico com id " + idMedico + " já possui consulta de " +
                inicioExistente.format(FORMATO_DATA_HORA) + " até " +
                inicioExistente.plusMinutes(DURACAO_EM_MINUTOS).format(FORMATO_DATA_HORA) +
                ", o que conflita com o horário solicitado (" + inicioSolicitado.format(FORMATO_DATA_HORA) + ").";
    }

    private Consulta consultaExistente(Long id, LocalDateTime dataHora, StatusConsulta status) {
        return new Consulta(id, dataHora, status, null, medicoModelo(), pacienteModelo());
    }

    private Consulta consultaModelo(Long id, ConsultaRequestDto dto) {
        return new Consulta(id, dto.dataHora(), dto.status(), dto.observacoes(), medicoModelo(), pacienteModelo());
    }

    private ConsultaRequestDto consultaDto() {
        return new ConsultaRequestDto(
                DATA_HORA_BASE,
                StatusConsulta.AGENDADA,
                "Consulta de rotina",
                1L,
                2L
        );
    }

    private Medico medicoModelo() {
        return new Medico(1L, "Arthur Cosmo", "123456PE", "arthur@email.com", "11999999999", true, null);
    }

    private Paciente pacienteModelo() {
        return new Paciente(2L, "Ana Silva", "12345678901", "ana@email.com", "11988887777",
                LocalDate.of(1990, 1, 1), true);
    }
}