package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.repository.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------

    private final EspecialidadeRepository especialidadeRepository;

    // MÉTODOS PADRÃO DO CRUD  -----------------------------------------------------------------------------------------

    public EspecialidadeResponseDto salvarEspecialidade(EspecialidadeRequestDto dto) {
        return converterParaResponseDto(especialidadeRepository.save(converterParaModel(dto)));
    }

    public EspecialidadeResponseDto buscarEspecialidadePorId(Long id) {

        // Busca especialidade no banco, se não encontrar, lança uma exception custom
        Especialidade especialidade = especialidadeRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id " + id)
        );

        return converterParaResponseDto(especialidade);
    }

    public List<EspecialidadeResponseDto> listarEspecialidades() {
        return especialidadeRepository.findAll().stream().map(this::converterParaResponseDto).toList();
    }

    public void deletarEspecialidade(Long id) {

        // Busca especialidade no banco, se não encontrar, lança uma exception custom
        Especialidade especialidade = especialidadeRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id " + id)
        );

        // todo: especialidade não pode ser excluída se tiver médicos vinculados a ela

        especialidadeRepository.deleteById(id);
    }

    public EspecialidadeResponseDto atualizarEspecialidade(Long id, EspecialidadeRequestDto dto) {

        especialidadeRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id " + id)
        );

        return converterParaResponseDto(especialidadeRepository.save(converterParaModel(id, dto)));

    }

    // MÉTODOS DE CONVERSÃO DE ENTIDADES -------------------------------------------------------------------------------

    Especialidade converterParaModel(EspecialidadeRequestDto dto) {
        return new Especialidade(
                null,
                dto.nome(),
                dto.descricao()
        );
    }

    // Polimorfismo usado para pode definir o ID da entidade na hora de atualizar no BD
    Especialidade converterParaModel(Long id, EspecialidadeRequestDto dto) {
        return new Especialidade(
                id,
                dto.nome(),
                dto.descricao()
        );
    }

    EspecialidadeResponseDto converterParaResponseDto(Especialidade model) {
        return new EspecialidadeResponseDto(
                model.getId(),
                model.getNome(),
                model.getDescricao()
        );
    }

}
