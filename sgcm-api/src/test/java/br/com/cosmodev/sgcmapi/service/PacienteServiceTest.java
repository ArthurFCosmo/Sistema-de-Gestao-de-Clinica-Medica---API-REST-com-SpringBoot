package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.PacienteRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PacienteResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

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

}
