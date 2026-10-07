package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.PacienteRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PacienteResponseDto;
import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private PacienteService pacienteService;

    // TESTANDO MÉTODOS DE CONVERSÃO DE ENTIDADES ----------------------------------------------------------------------

    @Test
    void deveConverterDtoParaModelComSucesso() {

        // Preparação
        LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        PacienteRequestDto requestDtoFalsaCorreta = new PacienteRequestDto(
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "(11) 99999-9999",
                dataNascimento
        );

        // Execução
        Paciente resultado = pacienteService.converterParaModel(requestDtoFalsaCorreta);

        // Confirmação
        assertAll(
                () -> assertNull(resultado.getId()),
                () -> assertEquals("Arthur Cosmo", resultado.getNome()),
                () -> assertEquals("12345678909", resultado.getCpf()),
                () -> assertEquals("arthur@email.com", resultado.getEmail()),
                () -> assertEquals("11999999999", resultado.getTelefone()),
                () -> assertEquals(dataNascimento, resultado.getData_nascimento()),
                () -> assertEquals(true, resultado.getAtivo())
        );
    }

    @Test
    void deveConverterDtoParaModelComIdEAtivoComSucesso() {

        // Preparação
        LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        PacienteRequestDto requestDtoFalsaCorreta = new PacienteRequestDto(
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "(11) 99999-9999",
                dataNascimento
        );

        // Execução
        Paciente resultado = pacienteService.converterParaModel(7L, requestDtoFalsaCorreta, false);

        // Confirmação
        assertAll(
                () -> assertEquals(7L, resultado.getId()),
                () -> assertEquals("Arthur Cosmo", resultado.getNome()),
                () -> assertEquals("12345678909", resultado.getCpf()),
                () -> assertEquals("arthur@email.com", resultado.getEmail()),
                () -> assertEquals("11999999999", resultado.getTelefone()),
                () -> assertEquals(dataNascimento, resultado.getData_nascimento()),
                () -> assertEquals(false, resultado.getAtivo())
        );
    }

    @Test
    void deveConverterModelParaDtoComSucesso() {

        // Preparação
        LocalDate dataNascimento = LocalDate.now().minusYears(36);
        Paciente pacienteModelFalsoCorreto = new Paciente(
                1L,
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "11999999999",
                dataNascimento,
                true
        );

        // Execução
        PacienteResponseDto resultado = pacienteService.converterParaResponseDto(pacienteModelFalsoCorreto);

        // Confirmação
        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Cosmo", resultado.nome()),
                () -> assertEquals("123.***.***-**", resultado.cpfMascarado()),
                () -> assertEquals("arthur@email.com", resultado.email()),
                () -> assertEquals("(11) 99999-9999", resultado.telefone()),
                () -> assertEquals(36, resultado.idade()),
                () -> assertEquals(true, resultado.ativo())
        );
    }

    // TESTANDO MÉTODOS PADRÃO DO CRUD ---------------------------------------------------------------------------------

    @Test
    void deveSalvarPacienteComSucesso() {

        // Preparação
        LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        PacienteRequestDto requestDtoFalsaCorreta = new PacienteRequestDto(
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "(11) 99999-9999",
                dataNascimento
        );

        Paciente pacienteModelFalsoCorreto = new Paciente(
                1L,
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "11999999999",
                dataNascimento,
                true
        );

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteModelFalsoCorreto);

        // Execução
        PacienteResponseDto resultado = pacienteService.salvarPaciente(requestDtoFalsaCorreta);

        // Confirmação
        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Cosmo", resultado.nome()),
                () -> assertEquals("123.***.***-**", resultado.cpfMascarado()),
                () -> assertEquals("arthur@email.com", resultado.email()),
                () -> assertEquals("(11) 99999-9999", resultado.telefone()),
                () -> assertEquals(36, resultado.idade()),
                () -> assertEquals(true, resultado.ativo())
        );

        verify(pacienteRepository, times(1)).save(any(Paciente.class));

    }

    @Test
    void deveBuscarPacientePorIdComSucesso() {

        // Preparação
        LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        Paciente pacienteModelFalsoCorreto = new Paciente(
                1L,
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "11999999999",
                dataNascimento,
                true
        );

        when(pacienteRepository.findById(any(Long.class))).thenReturn(Optional.of(pacienteModelFalsoCorreto));

        // Execução
        PacienteResponseDto resultado = pacienteService.buscarPacientePorId(1L);

        // Confirmação
        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Cosmo", resultado.nome()),
                () -> assertEquals("123.***.***-**", resultado.cpfMascarado()),
                () -> assertEquals("arthur@email.com", resultado.email()),
                () -> assertEquals("(11) 99999-9999", resultado.telefone()),
                () -> assertEquals(36, resultado.idade()),
                () -> assertEquals(true, resultado.ativo())
        );

        verify(pacienteRepository, times(1)).findById(any(Long.class));

    }

    @Test
    void deveLancarExceptionCasoNaoEncontrePacientePorId() {

        when(pacienteRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> pacienteService.buscarPacientePorId(999L)
        );

        assertEquals("Não foi encontrado paciente com o id 999", exception.getMessage());

    }

    @Test
    void deveListarTodosPacientesComSucesso() {

        // Preparação
        LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        Paciente pacienteModelFalsoCorreto1 = new Paciente(
                1L,
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "11999999999",
                dataNascimento,
                true
        );

        Paciente pacienteModelFalsoCorreto2 = new Paciente(
                2L,
                "Arthur Silva",
                "12345678900",
                "arthursilva@email.com",
                "1199999999",
                dataNascimento,
                true
        );

        List<Paciente> lista = List.of(pacienteModelFalsoCorreto1, pacienteModelFalsoCorreto2);


        when(pacienteRepository.findAll()).thenReturn(lista);

        //Execução

        List<PacienteResponseDto> resultado = pacienteService.listarPacientes();

        // Confirmação
        assertAll(
                () -> assertFalse(resultado.isEmpty()),

                () -> assertEquals(1L, resultado.get(0).id()),
                () -> assertEquals("Arthur Cosmo", resultado.get(0).nome()),
                () -> assertEquals("123.***.***-**", resultado.get(0).cpfMascarado()),
                () -> assertEquals("arthur@email.com", resultado.get(0).email()),
                () -> assertEquals("(11) 99999-9999", resultado.get(0).telefone()),
                () -> assertEquals(36, resultado.get(0).idade()),
                () -> assertEquals(true, resultado.get(0).ativo()),

                () -> assertEquals(2L, resultado.get(1).id()),
                () -> assertEquals("Arthur Silva", resultado.get(1).nome()),
                () -> assertEquals("123.***.***-**", resultado.get(1).cpfMascarado()),
                () -> assertEquals("arthursilva@email.com", resultado.get(1).email()),
                () -> assertEquals("(11) 9999-9999", resultado.get(1).telefone()),
                () -> assertEquals(36, resultado.get(1).idade()),
                () -> assertEquals(true, resultado.get(1).ativo())
        );

        verify(pacienteRepository, times(1)).findAll();

    }

    @Test
    void deveListarZeroPacientes() {

        // Preparação
        List<Paciente> listaVazia = new ArrayList<>();

        when(pacienteRepository.findAll()).thenReturn(listaVazia);

        // Execução
        List<PacienteResponseDto> resultado = pacienteService.listarPacientes();

        // Confirmação
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertTrue(resultado.isEmpty())
        );

        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    void deveDeletarPacienteComSucessoQuandoExistir() {

        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(consultaRepository.existsByPaciente_IdAndStatusIn(1L, StatusConsulta.ativos())).thenReturn(false);

        // Execução
        pacienteService.deletarPaciente(1L);

        // Confirmação
        verify(pacienteRepository, times(1)).existsById(1L);
        verify(consultaRepository, times(1)).existsByPaciente_IdAndStatusIn(1L, StatusConsulta.ativos());
        verify(pacienteRepository, times(1)).deleteById(1L);

    }

    @Test
    void deveLancarExceptionCasoNaoEncontrePacienteParaDeletar() {

        when(pacienteRepository.existsById(999L)).thenReturn(false);

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> pacienteService.deletarPaciente(999L)
        );

        assertEquals("Não foi encontrado paciente com o id 999", exception.getMessage());
        assertEquals("Não foi encontrado paciente com o id 999", exception.getMessage());
        verify(consultaRepository, never()).existsByPaciente_IdAndStatusIn(anyLong(), anyList());
        verify(pacienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveLancarExceptionAoDeletarPacienteComConsultasAtivas() {

        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(consultaRepository.existsByPaciente_IdAndStatusIn(1L, StatusConsulta.ativos())).thenReturn(true);

        RegraDeNegocioVioladaException exception = assertThrows(
                RegraDeNegocioVioladaException.class,
                () -> pacienteService.deletarPaciente(1L)
        );

        assertEquals(
                "O paciente com id 1 não pôde ser deletado, pois possui consultas ativas.",
                exception.getMessage()
        );
        verify(consultaRepository, times(1)).existsByPaciente_IdAndStatusIn(1L, StatusConsulta.ativos());
        verify(pacienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveAtualizarPacienteComSucesso() {

        // Preparação: Dto recebido na requisição
        LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        PacienteRequestDto requestDtoFalsaCorreta = new PacienteRequestDto(
                "Arthur Silva",
                "12345678900",
                "arthursilva@email.com",
                "(11) 99999-9999",
                dataNascimento
        );


        // Preparação: Mock do paciente salvo no banco
        Paciente pacienteModelFalsoCorreto = new Paciente(
                1L,
                "Arthur Cosmo",
                "12345678909",
                "arthur@email.com",
                "11999999999",
                dataNascimento,
                true
        );

        // Preparação: Mock da especialidade que será salva no banco após o métodó ser executado
        Paciente pacienteModelFalsoCorretoNovo = new Paciente(
                1L,
                "Arthur Silva",
                "12345678900",
                "arthursilva@email.com",
                "11999999999",
                dataNascimento,
                false
        );

        Long id = 1L;
        Boolean ativo = false;

        when(pacienteRepository.existsById(id)).thenReturn(true);

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteModelFalsoCorretoNovo);

        // Execução
        PacienteResponseDto resultado = pacienteService.atualizarPaciente(id, requestDtoFalsaCorreta, ativo);

        // Confirmação
        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Arthur Silva", resultado.nome()),
                () -> assertEquals("123.***.***-**", resultado.cpfMascarado()),
                () -> assertEquals("arthursilva@email.com", resultado.email()),
                () -> assertEquals("(11) 99999-9999", resultado.telefone()),
                () -> assertEquals(36, resultado.idade()),
                () -> assertEquals(false, resultado.ativo())
        );

        verify(pacienteRepository, times(1)).existsById(id);
        verify(pacienteRepository, times(1)).save(pacienteModelFalsoCorretoNovo);

    }

    @Test
    void deveLancarExceptionAoNaoEncontrarPacienteParaAtualizar() {

        // Preparação: DTO recebido na requisição
        LocalDate dataNascimento = LocalDate.of(1990, 5, 20);
        PacienteRequestDto requestDtoFalsaCorreta = new PacienteRequestDto(
                "Arthur Silva",
                "12345678900",
                "arthursilva@email.com",
                "(11) 99999-9999",
                dataNascimento
        );

        when(pacienteRepository.existsById(999L)).thenReturn(false);

        ElementoNaoEncontradoException exception = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> pacienteService.atualizarPaciente(999L, requestDtoFalsaCorreta, false)
        );

        assertEquals("Não foi encontrado paciente com o id 999", exception.getMessage());
        verify(pacienteRepository, times(1)).existsById(999L);
        verify(pacienteRepository, never()).save(any(Paciente.class));

    }

}
