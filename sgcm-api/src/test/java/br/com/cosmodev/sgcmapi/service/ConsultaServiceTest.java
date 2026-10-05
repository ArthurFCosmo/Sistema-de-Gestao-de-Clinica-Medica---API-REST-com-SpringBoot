package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.ConsultaRequestDto;
import br.com.cosmodev.sgcmapi.dtos.ConsultaResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Consulta;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaServiceTest {

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
                () -> assertEquals(dto.dataHora(), resultado.getData_hora()),
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
                () -> assertEquals(dto.dataHora(), resultado.getData_hora()),
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
                br.com.cosmodev.sgcmapi.enums.StatusConsulta.AGENDADA,
                "Consulta de rotina",
                medicoModelo(),
                pacienteModelo()
        );

        ConsultaResponseDto resultado = consultaService.converterParaResponseDto(consulta);

        assertAll(
                () -> assertEquals(3L, resultado.id()),
                () -> assertEquals(consulta.getData_hora(), resultado.dataHora()),
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
        Consulta consultaSalva = consultaModelo(3L, dto);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoModelo()));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(pacienteModelo()));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consultaSalva);

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
                        br.com.cosmodev.sgcmapi.enums.StatusConsulta.CONFIRMADA,
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
                () -> assertEquals(br.com.cosmodev.sgcmapi.enums.StatusConsulta.CONFIRMADA,
                        resultado.get(1).status())
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
        when(consultaRepository.findById(3L)).thenReturn(Optional.of(consultaModelo(3L, consultaDto())));

        consultaService.deletarConsultaPorId(3L);

        verify(consultaRepository).findById(3L);
        verify(consultaRepository).deleteById(3L);
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreConsultaParaDeletar() {
        when(consultaRepository.findById(999L)).thenReturn(Optional.empty());

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
                br.com.cosmodev.sgcmapi.enums.StatusConsulta.CONFIRMADA,
                "Consulta atualizada",
                1L,
                2L
        );
        when(consultaRepository.findById(3L)).thenReturn(Optional.of(consultaModelo(3L, consultaDto())));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoModelo()));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(pacienteModelo()));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consultaModelo(3L, dtoAtualizado));

        ConsultaResponseDto resultado = consultaService.atualizarConsulta(3L, dtoAtualizado);

        assertAll(
                () -> assertEquals(3L, resultado.id()),
                () -> assertEquals(dtoAtualizado.dataHora(), resultado.dataHora()),
                () -> assertEquals(dtoAtualizado.status(), resultado.status()),
                () -> assertEquals("Consulta atualizada", resultado.observacoes()),
                () -> assertEquals(1L, resultado.idMedico()),
                () -> assertEquals(2L, resultado.idPaciente())
        );
        verify(consultaRepository).findById(3L);
        verify(medicoRepository).findById(1L);
        verify(pacienteRepository).findById(2L);
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreConsultaParaAtualizar() {
        when(consultaRepository.findById(999L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> consultaService.atualizarConsulta(999L, consultaDto())
        );

        assertEquals("Não foi encontrada consulta com id 999", exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    // ENTIDADES PRE MONTADAS PARA ELIMINAR BOILERPLATE ----------------------------------------------------------------

    private Consulta consultaModelo(Long id, ConsultaRequestDto dto) {
        return new Consulta(id, dto.dataHora(), dto.status(), dto.observacoes(), medicoModelo(), pacienteModelo());
    }

    private ConsultaRequestDto consultaDto() {
        return new ConsultaRequestDto(
                LocalDateTime.of(2030, 5, 10, 14, 30),
                br.com.cosmodev.sgcmapi.enums.StatusConsulta.AGENDADA,
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
