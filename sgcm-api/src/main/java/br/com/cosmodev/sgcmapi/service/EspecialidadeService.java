package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.EspecialidadeRequestDto;
import br.com.cosmodev.sgcmapi.dtos.EspecialidadeResponseDto;
import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.repository.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------

    private final EspecialidadeRepository especialidadeRepository;

    // MÉTODOS PADRÃO DO CRUD  -----------------------------------------------------------------------------------------

    public EspecialidadeResponseDto salvarEspecialidade(EspecialidadeRequestDto dto) {
        return converterParaResponseDto(especialidadeRepository.save(converterParaModel(dto)));
    }

    // MÉTODOS DE CONVERSÃO DE ENTIDADES -------------------------------------------------------------------------------

    Especialidade converterParaModel(EspecialidadeRequestDto dto) {
        return new Especialidade(
                null,
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
