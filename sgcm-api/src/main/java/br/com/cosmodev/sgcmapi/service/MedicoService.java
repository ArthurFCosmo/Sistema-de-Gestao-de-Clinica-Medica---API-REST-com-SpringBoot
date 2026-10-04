package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.MedicoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.MedicoResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.repository.EspecialidadeRepository;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.cosmodev.sgcmapi.service.UtilsSgcm.*;

@Service
@RequiredArgsConstructor
public class MedicoService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------

    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;

    // MÉTODOS PADRÃO DO CRUD ------------------------------------------------------------------------------------------

    public MedicoResponseDto salvarMedico(MedicoRequestDto dto) {
        return converterParaResponseDto(medicoRepository.save(converterParaModel(dto)));
    }

    public MedicoResponseDto buscarMedicoPorId(Long id) {
        Medico medico = medicoRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado médico com o id " + id)
        );

        return converterParaResponseDto(medico);
    }

    public List<MedicoResponseDto> listarMedicos() {
        return medicoRepository.findAll().stream().map(this::converterParaResponseDto).toList();
    }

    public void deletarMedico(Long id) {
        medicoRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado médico com o id " + id)
        );

        // todo: médico não pode ser excluído se tiver consultas vinculadas a ele

        medicoRepository.deleteById(id);
    }

    public MedicoResponseDto atualizarMedico(Long id, MedicoRequestDto dto, Boolean ativo) {
        medicoRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado médico com o id " + id)
        );

        return converterParaResponseDto(medicoRepository.save(converterParaModel(id, dto, ativo)));
    }

    // MÉTODOS DE CONVERSÃO DE ENTIDADES -------------------------------------------------------------------------------

    Medico converterParaModel(MedicoRequestDto dto) {
        return new Medico(
                null,
                dto.nome(),
                tratarCrm(dto.crm()),
                dto.email(),
                tratarDeixandoSoNumeros(dto.telefone()),
                true,
                buscarEspecialidade(dto.idEspecialidade())
        );
    }

    // Sobrecarga usada para definir o ID e "Ativo" da entidade na hora de atualizar no BD
    Medico converterParaModel(Long id, MedicoRequestDto dto, Boolean ativo) {
        return new Medico(
                id,
                dto.nome(),
                tratarCrm(dto.crm()),
                dto.email(),
                tratarDeixandoSoNumeros(dto.telefone()),
                ativo,
                buscarEspecialidade(dto.idEspecialidade())
        );
    }

    MedicoResponseDto converterParaResponseDto(Medico model) {
        return new MedicoResponseDto(
                model.getId(),
                model.getNome(),
                formatarCrm(model.getCrm()),
                model.getEmail(),
                formatarTelefone(model.getTelefone()),
                model.getAtivo(),
                model.getEspecialidade().getNome()
        );
    }

    private Especialidade buscarEspecialidade(Long id) {
        return especialidadeRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada especialidade com o id " + id)
        );
    }
}
