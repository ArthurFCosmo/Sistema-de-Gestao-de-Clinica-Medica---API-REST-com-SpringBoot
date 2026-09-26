package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.PacienteRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PacienteResponseDto;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.cosmodev.sgcmapi.service.UtilsSgcm.*;

// iniciado às 10:08
@Service
@RequiredArgsConstructor
public class PacienteService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------
    private final PacienteRepository pacienteRepository;

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
