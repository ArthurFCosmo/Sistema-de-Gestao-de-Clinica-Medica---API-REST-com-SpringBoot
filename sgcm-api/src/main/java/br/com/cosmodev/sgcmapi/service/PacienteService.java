package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.PacienteRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PacienteResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.cosmodev.sgcmapi.service.UtilsSgcm.*;

// iniciado às 10:08
@Service
@RequiredArgsConstructor
public class PacienteService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------
    private final PacienteRepository pacienteRepository;

    // MÉTODOS PADRÃO DO CRUD ------------------------------------------------------------------------------------------

    public PacienteResponseDto salvarPaciente(PacienteRequestDto dto) {
        return converterParaResponseDto(pacienteRepository.save(converterParaModel(dto)));
    }

    public PacienteResponseDto buscarPacientePorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado paciente com o id " + id)
        );

        return converterParaResponseDto(paciente);
    }

    public List<PacienteResponseDto> listarPacientes() {
        return pacienteRepository.findAll().stream().map(this::converterParaResponseDto).toList();
    }

    public void deletarPaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado paciente com o id " + id)
        );

        // todo: Não pode ser excluído se possuir consultas com statuos "AGENDADA" ou "CONFIRMADA" no sistema.

        pacienteRepository.deleteById(id);

    }

    public PacienteResponseDto atualizarPaciente(Long id, PacienteRequestDto dto, Boolean ativo) {
        Paciente paciente = pacienteRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado paciente com o id " + id)
        );

        return converterParaResponseDto(pacienteRepository.save(converterParaModel(id, dto, ativo)));
    }



    // MÉTODOS DE CONVERSÃO DE ENTIDADES -------------------------------------------------------------------------------

    Paciente converterParaModel(PacienteRequestDto dto) {
        return new Paciente(
                null,
                dto.nome(),
                dto.cpf(),
                dto.email(),
                tratarTelefone(dto.telefone()),
                dto.dataNascimento(),
                true
        );
    }

    // Sobrecarga usada para poder definir o ID e "Ativo" da entidade na hora de atualizar no BD
    Paciente converterParaModel(Long id, PacienteRequestDto dto, Boolean ativo) {
        return new Paciente(
                id,
                dto.nome(),
                dto.cpf(),
                dto.email(),
                tratarTelefone(dto.telefone()),
                dto.dataNascimento(),
                ativo
        );
    }

    PacienteResponseDto converterParaResponseDto(Paciente model) {
        return new PacienteResponseDto(
                model.getId(),
                model.getNome(),
                mascararCpf(model.getCpf()),
                model.getEmail(),
                formatarTelefone(model.getTelefone()),
                calcularIdade(model.getData_nascimento()),
                model.getAtivo()
        );
    }
}
