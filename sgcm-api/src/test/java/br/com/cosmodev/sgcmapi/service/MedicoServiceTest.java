package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.MedicoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.MedicoResponseDto;
import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.EspecialidadeRepository;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private MedicoService medicoService;

    // TESTANDO MÉTODOS DE CONVERSÃO DE ENTIDADES ----------------------------------------------------------------------

    @Test
    void deveConverterDtoParaModelComSucesso() {

        // Preparação
        MedicoRequestDto requestDtoFalsaCorreta = new MedicoRequestDto(
                "Arthur Cosmo",
                "123456-PE",
                "arthur@email.com",
                "(11) 99999-9999",
                1L
        );

        Especialidade especialidade = new Especialidade(1L, "Cardiologista", "Especialista em cardiologia.");

        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));

        // Execução
        Medico resultado = medicoService.converterParaModel(requestDtoFalsaCorreta);

        // Confirmação
        assertAll(
                () -> assertNull(resultado.getId()),
                () -> assertEquals("Arthur Cosmo", resultado.getNome()),
                () -> assertEquals("123456PE", resultado.getCrm()),
                () -> assertEquals("arthur@email.com", resultado.getEmail()),
                () -> assertEquals("11999999999", resultado.getTelefone()),
                () -> assertTrue(resultado.getAtivo()),
                () -> assertEquals(especialidade, resultado.getEspecialidade())
        );
    }

    @Test
    void deveConverterDtoParaModelComIdEAtivoComSucesso() {

        // Preparação
        MedicoRequestDto requestDtoFalsaCorreta = new MedicoRequestDto(
                "Arthur Cosmo",
                "123456-PE",
                "arthur@email.com",
                "(11) 99999-9999",
                1L
        );
        Especialidade especialidade = new Especialidade(1L, "Cardiologista", "Especialista em cardiologia.");

        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));

        // Execução
        Medico resultado = medicoService.converterParaModel(7L, requestDtoFalsaCorreta, false);

        // Confirmação
        assertAll(
                () -> assertEquals(7L, resultado.getId()),
                () -> assertEquals("Arthur Cosmo", resultado.getNome()),
                () -> assertEquals("123456PE", resultado.getCrm()),
                () -> assertEquals("arthur@email.com", resultado.getEmail()),
                () -> assertEquals("11999999999", resultado.getTelefone()),
                () -> assertFalse(resultado.getAtivo()),
                () -> assertEquals(especialidade, resultado.getEspecialidade())
        );
    }

    @Test
    void deveConverterModelParaDtoComSucesso() {

        Especialidade especialidade = new Especialidade(1L, "Cardiologista", "Especialista em cardiologia.");

        Medico medico = new Medico(
                1L,
                "Arthur Cosmo",
                "123456PE",
                "arthur@email.com",
                "11999999999",
                true,
                especialidade
        );

        MedicoResponseDto resultado = medicoService.converterParaResponseDto(medico);

        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Cosmo", resultado.nome()),
                () -> assertEquals("123456-PE", resultado.crm()),
                () -> assertEquals("arthur@email.com", resultado.email()),
                () -> assertEquals("(11) 99999-9999", resultado.telefone()),
                () -> assertTrue(resultado.ativo()),
                () -> assertEquals("Cardiologista", resultado.especialidade())
        );
    }

    @Test
    void deveLancarExceptionCasoNaoEncontreEspecialidadeParaConversao() {

        MedicoRequestDto dto = medicoDto("Arthur Cosmo", "123456-PE", 999L);
        when(especialidadeRepository.findById(999L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> medicoService.converterParaModel(dto)
        );

        assertEquals("Não foi encontrada especialidade com o id 999", exception.getMessage());
    }

    // TESTANDO MÉTODOS PADRÃO DO CRUD ---------------------------------------------------------------------------------

    // POST ------------------------------------------------------------------------------------------------------------
    @Test
    void deveSalvarMedicoComSucesso() {

        // Preparação
        MedicoRequestDto dto = medicoDto("Arthur Cosmo", "123456-PE", 1L);
        Medico medicoSalvo = medicoModelo(1L, "Arthur Cosmo", "123456-PE", true);

        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(medicoSalvo.getEspecialidade()));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medicoSalvo);

        // Execução
        MedicoResponseDto resultado = medicoService.salvarMedico(dto);

        // Confirmação
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Cosmo", resultado.nome()),
                () -> assertEquals("123456-PE", resultado.crm()),
                () -> assertEquals("Cardiologista", resultado.especialidade())
        );

        verify(especialidadeRepository, times(1)).findById(1L);
        verify(medicoRepository, times(1)).save(any(Medico.class));
    }

    // GET ------------------------------------------------------------------------------------------------------------
    @Test
    void deveBuscarMedicoPorIdComSucesso() {

        // Preparação
        Medico medico = medicoModelo(1L, "Arthur Cosmo", "123456-PE", true);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        // Execução
        MedicoResponseDto resultado = medicoService.buscarMedicoPorId(1L);

        // Confirmação
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Cosmo", resultado.nome()),
                () -> assertEquals("123456-PE", resultado.crm()),
                () -> assertEquals("Cardiologista", resultado.especialidade())
        );

        verify(medicoRepository, times(1)).findById(1L);
    }

    // GET ------------------------------------------------------------------------------------------------------------
    @Test
    void deveLancarExceptionCasoNaoEncontreMedicoPorId() {

        when(medicoRepository.findById(999L)).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> medicoService.buscarMedicoPorId(999L)
        );

        assertEquals("Não foi encontrado médico com o id 999", exception.getMessage());
    }

    // GET ------------------------------------------------------------------------------------------------------------
    @Test
    void deveListarMedicosPersonalizadoComPaginacao() {

        // Preparação
        Pageable pageable = PageRequest.of(0, 20, Sort.by("nome"));
        List<Medico> medicos = List.of(
                medicoModelo(1L, "Arthur Cosmo", "123456PE", true),
                medicoModelo(2L, "Ana Silva", "654321SP", false)
        );
        Page<Medico> pagina = new PageImpl<>(medicos, pageable, 2);
        when(medicoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagina);

        // Execução
        Page<MedicoResponseDto> resultado = medicoService.listarPersonalizado(null, pageable);

        // Confirmação
        assertAll(
                () -> assertEquals(2, resultado.getTotalElements()),
                () -> assertEquals(1, resultado.getTotalPages()),
                () -> assertEquals(2, resultado.getContent().size()),
                () -> assertEquals("Arthur Cosmo", resultado.getContent().get(0).nome()),
                () -> assertEquals("Cardiologista", resultado.getContent().get(0).especialidade()),
                () -> assertEquals("Ana Silva", resultado.getContent().get(1).nome()),
                () -> assertFalse(resultado.getContent().get(1).ativo())
        );

        verify(especialidadeRepository, never()).existsById(anyLong());
        verify(medicoRepository).findAll(any(Specification.class), eq(pageable));
    }

    // GET ------------------------------------------------------------------------------------------------------------
    @Test
    void deveFiltrarMedicosPorEspecialidade() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Medico> pagina = new PageImpl<>(
                List.of(medicoModelo(1L, "Arthur Cosmo", "123456PE", true)),
                pageable,
                1
        );
        when(especialidadeRepository.existsById(1L)).thenReturn(true);
        when(medicoRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagina);

        Page<MedicoResponseDto> resultado = medicoService.listarPersonalizado(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Arthur Cosmo", resultado.getContent().get(0).nome());
        verify(especialidadeRepository).existsById(1L);
        verify(medicoRepository).findAll(any(Specification.class), eq(pageable));
    }

    // GET ------------------------------------------------------------------------------------------------------------
    @Test
    void deveLancarExceptionAoFiltrarPorEspecialidadeInexistente() {

        Pageable pageable = PageRequest.of(0, 20);
        when(especialidadeRepository.existsById(999L)).thenReturn(false);

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> medicoService.listarPersonalizado(999L, pageable)
        );

        assertEquals("Não foi encontrado especialidade com o id 999", exception.getMessage());
        verify(medicoRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    // DELETE ---------------------------------------------------------------------------------------------------------
    @Test
    void deveDeletarMedicoComSucessoQuandoExistir() {

        // Preparação
        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(consultaRepository.existsByMedico_IdAndStatusIn(1L, StatusConsulta.ativos())).thenReturn(false);

        // Execução
        medicoService.deletarMedico(1L);

        // Confirmação
        verify(medicoRepository, times(1)).existsById(1L);
        verify(consultaRepository, times(1)).existsByMedico_IdAndStatusIn(1L, StatusConsulta.ativos());
        verify(medicoRepository, times(1)).deleteById(1L);
    }

    // DELETE ---------------------------------------------------------------------------------------------------------
    @Test
    void deveLancarExceptionCasoNaoEncontreMedicoParaDeletar() {

        when(medicoRepository.existsById(999L)).thenReturn(false);

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> medicoService.deletarMedico(999L)
        );

        assertEquals("Não foi encontrado médico com o id 999", exception.getMessage());
        verify(medicoRepository, times(1)).existsById(999L);
        verify(consultaRepository, never()).existsByMedico_IdAndStatusIn(anyLong(), anyList());
        verify(medicoRepository, never()).deleteById(anyLong());
    }


    // DELETE ---------------------------------------------------------------------------------------------------------
    @Test
    void deveLancarExceptionAoDeletarMedicoComConsultasAtivas() {

        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(consultaRepository.existsByMedico_IdAndStatusIn(1L, StatusConsulta.ativos())).thenReturn(true);

        RegraDeNegocioVioladaException exception = assertThrows(
                RegraDeNegocioVioladaException.class,
                () -> medicoService.deletarMedico(1L)
        );

        assertEquals(
                "O medico com id 1 não pôde ser deletado, pois possui consultas ativas.",
                exception.getMessage()
        );
        verify(consultaRepository, times(1)).existsByMedico_IdAndStatusIn(1L, StatusConsulta.ativos());
        verify(medicoRepository, never()).deleteById(anyLong());
    }


    // UPDATE ---------------------------------------------------------------------------------------------------------
    @Test
    void deveAtualizarMedicoComSucesso() {

        // Preparação: DTO recebido na requisição
        MedicoRequestDto dto = medicoDto("Arthur Silva", "654321-SP", 1L);
        Medico medicoAtual = medicoModelo(1L, "Arthur Cosmo", "123456PE", true);
        Medico medicoAtualizado = medicoModelo(1L, "Arthur Silva", "654321SP", false);

        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(medicoAtualizado.getEspecialidade()));
        when(medicoRepository.save(any(Medico.class))).thenReturn(medicoAtualizado);

        // Execução
        MedicoResponseDto resultado = medicoService.atualizarMedico(1L, dto, false);

        // Confirmação
        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Silva", resultado.nome()),
                () -> assertEquals("654321-SP", resultado.crm()),
                () -> assertEquals("arthur@email.com", resultado.email()),
                () -> assertEquals("(11) 99999-9999", resultado.telefone()),
                () -> assertFalse(resultado.ativo()),
                () -> assertEquals("Cardiologista", resultado.especialidade())
        );

        verify(medicoRepository, times(1)).existsById(1L);
        verify(medicoRepository, times(1)).save(medicoAtualizado);
    }


    // UPDATE ---------------------------------------------------------------------------------------------------------
    @Test
    void deveLancarExceptionCasoNaoEncontreMedicoParaAtualizar() {

        MedicoRequestDto dto = medicoDto("Arthur Silva", "654321-SP", 1L);
        when(medicoRepository.existsById(999L)).thenReturn(false);

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> medicoService.atualizarMedico(999L, dto, false)
        );

        assertEquals("Não foi encontrado médico com o id 999", exception.getMessage());
        verify(medicoRepository, times(1)).existsById(999L);
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    // ENTIDADES PRE MONTADAS PARA ELIMINAR BOILERPLATE ----------------------------------------------------------------

    private MedicoRequestDto medicoDto(String nome, String crm, Long idEspecialidade) {
        return new MedicoRequestDto(nome, crm, "arthur@email.com", "(11) 99999-9999", idEspecialidade);
    }

    private Medico medicoModelo(Long id, String nome, String crm, Boolean ativo) {
        Especialidade especialidade = new Especialidade(1L, "Cardiologista", "Especialista em cardiologia.");
        return new Medico(id, nome, crm, "arthur@email.com", "11999999999", ativo, especialidade);
    }
}
