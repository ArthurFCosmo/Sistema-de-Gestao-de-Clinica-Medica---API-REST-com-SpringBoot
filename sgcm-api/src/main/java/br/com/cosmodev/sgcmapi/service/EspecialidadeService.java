package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.repository.EspecialidadeRepository;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------

    private final EspecialidadeRepository especialidadeRepository;
    private final MedicoRepository medicoRepository;

    // MÉTODOS PADRÃO DO CRUD ------------------------------------------------------------------------------------------

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
        if (!especialidadeRepository.existsById(id)) {
            throw new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id " + id);
        }

        // Bloqueia a exclusão de Especialidades que possuem médicos vinculados
        if (medicoRepository.existsByEspecialidade_Id(id)) {
            throw new RegraDeNegocioVioladaException("Especialidade com id " + id + " não pôde ser deletada pois existem médicos vinculados a ela.");
        }

        especialidadeRepository.deleteById(id);
    }

    public EspecialidadeResponseDto atualizarEspecialidade(Long id, EspecialidadeRequestDto dto) {

        if (!especialidadeRepository.existsById(id)) {
            throw new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id " + id);
        }

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

    // Sobrecarga usada para poder definir o ID da entidade na hora de atualizar no BD
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
