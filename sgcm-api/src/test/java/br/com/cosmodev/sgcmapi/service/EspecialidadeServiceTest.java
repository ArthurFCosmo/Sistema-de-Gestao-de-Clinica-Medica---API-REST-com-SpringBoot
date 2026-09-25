package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.repository.EspecialidadeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EspecialidadeServiceTest {

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @InjectMocks
    private EspecialidadeService especialidadeService;

    // TESTANDO MÉTODOS DE CONVERSÃO DE ENTIDADES ----------------------------------------------------------------------

    @Test
    void deveConverterDtoParaModelComSucesso() {

        EspecialidadeRequestDto requestDtoFalsaCorreta = new EspecialidadeRequestDto(
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        Especialidade resultado = especialidadeService.converterParaModel(requestDtoFalsaCorreta);

        assertAll(
                () -> assertEquals("Cardiologista", resultado.getNome()),
                () -> assertEquals("Especialista na saúde do coração e do sistema circulatório.", resultado.getDescricao())
                );

    }

    @Test
    void deveConverterModelParaDtoComSucesso() {

        Especialidade especialidadeModelFalsoCorreto = new Especialidade(
                1L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        EspecialidadeResponseDto resultado = especialidadeService.converterParaResponseDto(especialidadeModelFalsoCorreto);

        assertAll(
                () -> assertEquals(1, resultado.id()),
                () -> assertEquals("Cardiologista", resultado.nome()),
                () -> assertEquals("Especialista na saúde do coração e do sistema circulatório.", resultado.descricao())
        );

    }

    // TESTANDO MÉTODOS PADRÃO DO CRUD ---------------------------------------------------------------------------------

    // salvarEspecialidade() POST
    @Test
    void deveSalvarEspecialidadeComSucesso() {

        // Preparação
        EspecialidadeRequestDto requestDtoFalsaCorreta = new EspecialidadeRequestDto(
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        Especialidade especialidadeSalvaFalsa = new Especialidade(
                1L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        when(especialidadeRepository.save(any(Especialidade.class))).thenReturn(especialidadeSalvaFalsa);

        // Execução: Deve retornar corretamente um ResponseDto baseado na request
        EspecialidadeResponseDto resultado = especialidadeService.salvarEspecialidade(requestDtoFalsaCorreta);

        // Confirmação
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.id()),
                () -> assertEquals("Cardiologista", resultado.nome()),
                () -> assertEquals("Especialista na saúde do coração e do sistema circulatório.", resultado.descricao())
        );

        verify(especialidadeRepository, times(1)).save(any(Especialidade.class));
    }

    // buscarEspecialidadePorId() GET
    @Test
    void deveBuscarEspecialidadePorIdComSucesso () {

        // Preparação
        Long idRequestFalso = 1L;

        Especialidade especialidadeSalvaFalsa = new Especialidade(
                1L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        when(especialidadeRepository.findById(any(Long.class))).thenReturn(Optional.of(especialidadeSalvaFalsa));

        // Execução: Deve retornar corretamente um ResponseDto baseado na request
        EspecialidadeResponseDto resultado = especialidadeService.buscarEspecialidadePorId(idRequestFalso);

        // Confirmação
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.id()),
                () -> assertEquals("Cardiologista", resultado.nome()),
                () -> assertEquals("Especialista na saúde do coração e do sistema circulatório.", resultado.descricao())
        );

        verify(especialidadeRepository, times(1)).findById(any(Long.class));
    };

    @Test
    void deveLançarExceptionCasoNaoEncontreEspecialidadePorId () {

        // Preparação: Retornando valor vazio para busca no BD usando a classe Optional padrão do repository
        when(especialidadeRepository.findById(999L)).thenReturn(Optional.empty());

        // Confirmação: Validando se o métodó lança a exception
        ElementoNaoEncontradoException excecao = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> especialidadeService.buscarEspecialidadePorId(999L)
        );

        // Confirmação: Validando se a exception foi montada com a mensagem correta (Que é o único parâmetro legível antes do tratamento)
        assertAll(
                () -> assertEquals("Não foi encontrada especialidade com o id 999", excecao.getMessage())
        );
    }

}
