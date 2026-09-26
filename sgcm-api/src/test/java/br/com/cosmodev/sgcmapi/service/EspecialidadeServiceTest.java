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

import java.util.List;
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

    // buscarEspecialidadePorId() GET com sucesso
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
    }

    // buscarEspecialidadePorId() GET com erro
    @Test
    void deveLancarExceptionCasoNaoEncontreEspecialidadePorId () {

        // Preparação: Retornando valor vazio para busca no BD usando a classe Optional padrão do repository
        when(especialidadeRepository.findById(999L)).thenReturn(Optional.empty());

        // Confirmação: Validando se o métodó lança a exception
        ElementoNaoEncontradoException excecao = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> especialidadeService.buscarEspecialidadePorId(999L)
        );

        // Confirmação: Validando se a exception foi montada com a mensagem correta (Que é o único parâmetro legível antes do tratamento)
        assertEquals("Não foi encontrada especialidade com o id 999", excecao.getMessage());

    }

    // listarEspecialidades() GET com sucesso
    @Test
    void deveListarTodasEspecialidadesComSucesso() {

        // Preparação
        Especialidade especialidadeSalvaFalsa1 = new Especialidade(
                1L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        Especialidade especialidadeSalvaFalsa2 = new Especialidade(
                2L,
                "Oncologista",
                "Especialista em diagnóstico e tratamentos de câncer."
        );

        List<Especialidade> listaDeEspecialidadesCorretas = List.of(especialidadeSalvaFalsa1, especialidadeSalvaFalsa2);

        // Preparação: Mockando lista de especialidades que virá como modelos diretamente do bd
        when(especialidadeRepository.findAll()).thenReturn(listaDeEspecialidadesCorretas);


        // Execução
        List<EspecialidadeResponseDto> resultado = especialidadeService.listarEspecialidades();

        // Confirmação: Deve retornar uma lista de ResponsesDTO's montados corretamente
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals("Cardiologista", resultado.get(0).nome()),
                () -> assertEquals(1, resultado.get(0).id()),
                () -> assertEquals("Oncologista", resultado.get(1).nome()),
                () -> assertEquals(2, resultado.get(1).id())
        );

        verify(especialidadeRepository, times(1)).findAll();

    }

    // listarEspecialidades() GET deve retornar uma lista vazia
    @Test
    void deveListarZeroEspecialidades() {

        // Preparação: Criando lista vazia e mockando o métodó do repository que trará ela
        List<Especialidade> listaVaziaDeEspecialidades = List.of();

        when(especialidadeRepository.findAll()).thenReturn(listaVaziaDeEspecialidades);

        // Execução
        List<EspecialidadeResponseDto> resultado = especialidadeService.listarEspecialidades();

        //Confirmação
        assertTrue(resultado.isEmpty());

        verify(especialidadeRepository, times(1)).findAll();
    }

    // deletarEspecialidade() DELETE
    @Test
    void deveDeletarEspecialidadeComSucessoQuandoExistir() {

        // Preparação
        Long id = 1L;
        Especialidade especialidade = new Especialidade();


        when(especialidadeRepository.findById(id))
                .thenReturn(Optional.of(especialidade));

        // Execução
        especialidadeService.deletarEspecialidade(id);

        // Confirmação: Se o métodó do repository foi chamado
        verify(especialidadeRepository, times(1)).findById(id);
        verify(especialidadeRepository, times(1)).deleteById(id);

    }

    // deletarEspecialidade() DELETE
    @Test
    void deveLancarExceptionCasoNaoEncontreEspecialidadeParaDeletar () {

        // Preparação: Retornando valor vazio para busca no BD usando a classe Optional padrão do repository
        when(especialidadeRepository.findById(999L)).thenReturn(Optional.empty());

        // Confirmação: Validando se o métodó lança a exception
        ElementoNaoEncontradoException excecao = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> especialidadeService.deletarEspecialidade(999L)
        );

        // Confirmação: Validando se a exception foi montada com a mensagem correta (Que é o único parâmetro legível antes do tratamento)

        assertEquals("Não foi encontrada especialidade com o id 999", excecao.getMessage());

    }

    @Test
    void deveAtualizarEspecialidadeComSucesso() {

        // Preparação: Dto recebido na requisição
        EspecialidadeRequestDto requestDtoCorretaMock = new EspecialidadeRequestDto(
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        // Preparação: Mock da especialidade salva no banco
        Especialidade especialidadeSalvaMock = new Especialidade(
                2L,
                "Oncologista",
                "Especialista em diagnóstico e tratamentos de câncer."
        );

        // Preparação: Mock da especialidade que será salva no banco após o métodó ser executado
        Especialidade especialidadeSalvaMockNova = new Especialidade(
                2L,
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        Long id = 2L;


        // Preparação: Mock do métodó de buscar no BD retorando notnull
        when(especialidadeRepository.findById(2L)).thenReturn(Optional.of(especialidadeSalvaMock));

        // Preparação: Mock da especialidade sendo salva depois de ser convertida de dto para model
        when(especialidadeRepository.save(any(Especialidade.class))).thenReturn(especialidadeSalvaMockNova);

        // Execução
        EspecialidadeResponseDto resultado = especialidadeService.atualizarEspecialidade(2L, requestDtoCorretaMock);

        // Confirmação
        assertAll(
                () -> assertEquals(2L, resultado.id()),
                () -> assertEquals("Cardiologista", resultado.nome()),
                () -> assertEquals("Especialista na saúde do coração e do sistema circulatório.", resultado.descricao())
        );

        verify(especialidadeRepository, times(1)).findById(id);
        verify(especialidadeRepository, times(1)).save(especialidadeSalvaMockNova);

    }

    @Test
    void deveLancarExceptionAoNaoEncontrarEspecialdiadeParaAtualizar() {

        // Preparação: Dto recebido na requisição
        EspecialidadeRequestDto requestDtoCorretaMock = new EspecialidadeRequestDto(
                "Cardiologista",
                "Especialista na saúde do coração e do sistema circulatório."
        );

        // Preparação: Retornando valor vazio para busca no BD usando a classe Optional padrão do repository
        when(especialidadeRepository.findById(999L)).thenReturn(Optional.empty());

        // Confirmação: Validando se o métodó lança a exception
        ElementoNaoEncontradoException excecao = assertThrows(
                ElementoNaoEncontradoException.class,
                () -> especialidadeService.atualizarEspecialidade(999L, requestDtoCorretaMock)
        );

        // Confirmação: Validando se a exception foi montada com a mensagem correta (Que é o único parâmetro legível antes do tratamento)

        assertEquals("Não foi encontrada especialidade com o id 999", excecao.getMessage());

    }

}
