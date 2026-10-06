package br.com.cosmodev.sgcmapi.service;

import org.junit.jupiter.api.extension.ExtendWith;
import br.com.cosmodev.sgcmapi.dtos.PrescricaoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PrescricaoResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Consulta;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.model.Prescricao;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.PrescricaoRepository;
import org.junit.jupiter.api.Test;
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
public class PrescricaoServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private PrescricaoRepository prescricaoRepository;

    @InjectMocks
    private PrescricaoService prescricaoService;

    // TESTANDO MÉTODOS DE CONVERSÃO DE ENTIDADES ----------------------------------------------------------------------

    @Test
    void deveConverterDtoParaModelComSucesso() {
        PrescricaoRequestDto dto = prescricaoDto();
        Consulta consulta = consultaModelo();
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        LocalDate hoje = LocalDate.now();

        Prescricao resultado = prescricaoService.converterParaModel(dto);

        assertAll(
                () -> assertNull(resultado.getId()),
                () -> assertEquals(dto.descricao(), resultado.getDescricao()),
                () -> assertEquals(dto.medicamentos(), resultado.getMedicamentos()),
                () -> assertEquals(hoje, resultado.getData_emissao()),
                () -> assertEquals(consulta, resultado.getConsulta())
        );
        verify(consultaRepository).findById(1L);
    }

    @Test
    void deveConverterDtoParaModelComIdEDataDeEmissaoComSucesso() {
        PrescricaoRequestDto dto = prescricaoDto();
        Consulta consulta = consultaModelo();
        LocalDate dataEmissao = LocalDate.of(2030, 5, 10);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        Prescricao resultado = prescricaoService.converterParaModel(7L, dto, dataEmissao);

        assertAll(
                () -> assertEquals(7L, resultado.getId()),
                () -> assertEquals(dto.descricao(), resultado.getDescricao()),
                () -> assertEquals(dto.medicamentos(), resultado.getMedicamentos()),
                () -> assertEquals(dataEmissao, resultado.getData_emissao()),
                () -> assertEquals(consulta, resultado.getConsulta())
        );
        verify(consultaRepository).findById(1L);
    }

    @Test
    void deveConverterModelParaDtoComSucesso() {
        Prescricao prescricao = new Prescricao(
                7L,
                "Tratamento para dor",
                "Paracetamol 500mg",
                LocalDate.of(2030, 5, 10),
                consultaModelo()
        );

        PrescricaoResponseDto resultado = prescricaoService.converterParaResponseDto(prescricao);

        assertAll(
                () -> assertEquals(7L, resultado.id()),
                () -> assertEquals(1L, resultado.idConsulta()),
                () -> assertEquals("Arthur Cosmo", resultado.nomeMedico()),
                () -> assertEquals("123456-PE", resultado.crmMedico()),
                () -> assertEquals("Ana Silva", resultado.nomePaciente()),
                () -> assertEquals(LocalDateTime.of(2030, 5, 10, 14, 30), resultado.dataHoraConsulta()),
                () -> assertEquals("Tratamento para dor", resultado.descricao()),
                () -> assertEquals("Paracetamol 500mg", resultado.medicamentos()),
                () -> assertEquals(LocalDate.of(2030, 5, 10), resultado.dataEmissao())
        );
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreConsultaParaConversao() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> prescricaoService.converterParaModel(prescricaoDto())
        );

        assertEquals("Não foi encontrada consulta com id 1", exception.getMessage());
        verify(consultaRepository).findById(1L);
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreConsultaParaConversaoDeAtualizacao() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> prescricaoService.converterParaModel(7L, prescricaoDto(), LocalDate.of(2030, 5, 10))
        );

        assertEquals("Não foi encontrada consulta com id 1", exception.getMessage());
        verify(consultaRepository).findById(1L);
    }

    // TESTANDO MÉTODOS PADRÃO DO CRUD ---------------------------------------------------------------------------------

    @Test
    void deveSalvarPrescricaoComSucesso() {
        PrescricaoRequestDto dto = prescricaoDto();
        Prescricao prescricaoSalva = prescricaoModelo(7L, dto, LocalDate.now());
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consultaModelo()));
        when(prescricaoRepository.save(any(Prescricao.class))).thenReturn(prescricaoSalva);

        PrescricaoResponseDto resultado = prescricaoService.salvarPrescricao(dto);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(7L, resultado.id()),
                () -> assertEquals(1L, resultado.idConsulta()),
                () -> assertEquals("Arthur Cosmo", resultado.nomeMedico()),
                () -> assertEquals("123456-PE", resultado.crmMedico()),
                () -> assertEquals("Ana Silva", resultado.nomePaciente()),
                () -> assertEquals(dto.descricao(), resultado.descricao()),
                () -> assertEquals(dto.medicamentos(), resultado.medicamentos()),
                () -> assertEquals(LocalDate.now(), resultado.dataEmissao())
        );
        verify(consultaRepository).findById(1L);
        verify(prescricaoRepository).save(any(Prescricao.class));
    }

    @Test
    void deveBuscarPrescricaoPeloIdComSucesso() {
        when(prescricaoRepository.findById(7L))
                .thenReturn(Optional.of(prescricaoModelo(7L, prescricaoDto(), LocalDate.of(2030, 5, 10))));

        PrescricaoResponseDto resultado = prescricaoService.buscarPrescricaoPeloId(7L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(7L, resultado.id()),
                () -> assertEquals(1L, resultado.idConsulta()),
                () -> assertEquals("Arthur Cosmo", resultado.nomeMedico()),
                () -> assertEquals("Ana Silva", resultado.nomePaciente()),
                () -> assertEquals("Tratamento para dor", resultado.descricao()),
                () -> assertEquals(LocalDate.of(2030, 5, 10), resultado.dataEmissao())
        );
        verify(prescricaoRepository).findById(7L);
    }

    @Test
    void deveLancarExceptionCasoNaoEncontrePrescricaoPorId() {
        when(prescricaoRepository.findById(999L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> prescricaoService.buscarPrescricaoPeloId(999L)
        );

        assertEquals("Não foi encontrada prescrição com id 999", exception.getMessage());
        verify(prescricaoRepository).findById(999L);
    }

    @Test
    void deveListarTodasPrescricoesComSucesso() {
        Prescricao primeiraPrescricao = prescricaoModelo(7L, prescricaoDto(), LocalDate.of(2030, 5, 10));
        Prescricao segundaPrescricao = prescricaoModelo(8L, new PrescricaoRequestDto(
                "Tratamento para alergia",
                "Loratadina 10mg",
                1L
        ), LocalDate.of(2030, 5, 11));
        when(prescricaoRepository.findAll()).thenReturn(List.of(primeiraPrescricao, segundaPrescricao));

        List<PrescricaoResponseDto> resultado = prescricaoService.listarPrescricoes();

        assertAll(
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals(7L, resultado.get(0).id()),
                () -> assertEquals("Tratamento para dor", resultado.get(0).descricao()),
                () -> assertEquals(8L, resultado.get(1).id()),
                () -> assertEquals("Tratamento para alergia", resultado.get(1).descricao()),
                () -> assertEquals("Loratadina 10mg", resultado.get(1).medicamentos())
        );
        verify(prescricaoRepository).findAll();
    }

    @Test
    void deveListarZeroPrescricoes() {
        when(prescricaoRepository.findAll()).thenReturn(List.of());

        List<PrescricaoResponseDto> resultado = prescricaoService.listarPrescricoes();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(prescricaoRepository).findAll();
    }

    @Test
    void deveDeletarPrescricaoComSucessoQuandoExistir() {
        when(prescricaoRepository.findById(7L))
                .thenReturn(Optional.of(prescricaoModelo(7L, prescricaoDto(), LocalDate.of(2030, 5, 10))));

        prescricaoService.deletarPrescricaoPeloId(7L);

        verify(prescricaoRepository).findById(7L);
        verify(prescricaoRepository).deleteById(7L);
    }

    @Test
    void deveLancarExceptionCasoNaoEncontrePrescricaoParaDeletar() {
        when(prescricaoRepository.findById(999L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> prescricaoService.deletarPrescricaoPeloId(999L)
        );

        assertEquals("Não foi encontrada prescrição com id 999", exception.getMessage());
        verify(prescricaoRepository, never()).deleteById(any());
    }

    @Test
    void deveAtualizarPrescricaoComSucesso() {
        PrescricaoRequestDto dtoAtualizado = new PrescricaoRequestDto(
                "Tratamento atualizado",
                "Ibuprofeno 400mg",
                1L
        );
        LocalDate dataEmissao = LocalDate.of(2030, 5, 12);
        when(prescricaoRepository.findById(7L))
                .thenReturn(Optional.of(prescricaoModelo(7L, prescricaoDto(), LocalDate.of(2030, 5, 10))));
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consultaModelo()));
        when(prescricaoRepository.save(any(Prescricao.class)))
                .thenReturn(prescricaoModelo(7L, dtoAtualizado, dataEmissao));

        PrescricaoResponseDto resultado = prescricaoService.atualizarPrescricao(7L, dtoAtualizado, dataEmissao);

        assertAll(
                () -> assertEquals(7L, resultado.id()),
                () -> assertEquals(1L, resultado.idConsulta()),
                () -> assertEquals("Tratamento atualizado", resultado.descricao()),
                () -> assertEquals("Ibuprofeno 400mg", resultado.medicamentos()),
                () -> assertEquals(dataEmissao, resultado.dataEmissao())
        );
        verify(prescricaoRepository).findById(7L);
        verify(consultaRepository).findById(1L);
        verify(prescricaoRepository).save(any(Prescricao.class));
    }

    @Test
    void deveLancarExceptionCasoNaoEncontrePrescricaoParaAtualizar() {
        when(prescricaoRepository.findById(999L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> prescricaoService.atualizarPrescricao(999L, prescricaoDto(), LocalDate.of(2030, 5, 10))
        );

        assertEquals("Não foi encontrada prescrição com id 999", exception.getMessage());
        verify(prescricaoRepository, never()).save(any(Prescricao.class));
        verify(consultaRepository, never()).findById(any());
    }

    // ENTIDADES PRE MONTADAS PARA ELIMINAR BOILERPLATE ----------------------------------------------------------------

    private Prescricao prescricaoModelo(Long id, PrescricaoRequestDto dto, LocalDate dataEmissao) {
        return new Prescricao(id, dto.descricao(), dto.medicamentos(), dataEmissao, consultaModelo());
    }

    private PrescricaoRequestDto prescricaoDto() {
        return new PrescricaoRequestDto(
                "Tratamento para dor",
                "Paracetamol 500mg",
                1L
        );
    }

    private Consulta consultaModelo() {
        Medico medico = new Medico(2L, "Arthur Cosmo", "123456PE", "arthur@email.com",
                "11999999999", true, null);
        Paciente paciente = new Paciente(3L, "Ana Silva", "12345678901", "ana@email.com",
                "11988887777", LocalDate.of(1990, 1, 1), true);
        return new Consulta(
                1L,
                LocalDateTime.of(2030, 5, 10, 14, 30),
                br.com.cosmodev.sgcmapi.enums.StatusConsulta.AGENDADA,
                "Consulta de rotina",
                medico,
                paciente
        );
    }
}
