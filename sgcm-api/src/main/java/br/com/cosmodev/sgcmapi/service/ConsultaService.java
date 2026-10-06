package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.ConsultaRequestDto;
import br.com.cosmodev.sgcmapi.dtos.ConsultaResponseDto;
import br.com.cosmodev.sgcmapi.dtos.MedicoResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Consulta;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.cosmodev.sgcmapi.service.UtilsSgcm.formatarCrm;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------
    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    // MÉTODOS PADRÃO DO CRUD ------------------------------------------------------------------------------------------

    public ConsultaResponseDto salvarConsulta(ConsultaRequestDto dto) {

        // todo: Não podem criar (marcar) consultas num intervalo sobrebosto de 20 minutos entre elas

        return converterParaResponseDto(consultaRepository.save(converterParaModel(dto)));
    }

    public ConsultaResponseDto buscarConsultaPorId(Long id) {
        return converterParaResponseDto(consultaRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada consulta com id " + id)
        ));
    }

    public List<ConsultaResponseDto> listarConsultas() {
        return consultaRepository.findAll().stream().map(this::converterParaResponseDto).toList();
    };

    public void deletarConsultaPorId(Long id) {
        consultaRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada consulta com id " + id)
        );

        consultaRepository.deleteById(id);
    }

    public ConsultaResponseDto atualizarConsulta(Long id, ConsultaRequestDto dto) {
        consultaRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada consulta com id " + id)
        );

        // todo: Não podem criar (marcar) consultas num intervalo sobrebosto de 20 minutos entre elas

        return converterParaResponseDto(consultaRepository.save(converterParaModel(id, dto)));
    }

    // MÉTODOS DE CONVERSÃO DE ENTIDADES -------------------------------------------------------------------------------

    Consulta converterParaModel(ConsultaRequestDto dto) {
        return new Consulta(
                null,
                dto.dataHora(),
                dto.status(),
                dto.observacoes(),
                buscarMedicoPorId(dto.idMedico()),
                buscarPacientePorId(dto.idPaciente())
        );
    }

    // Sobrecarga do métodó para poder definir o Id na hora de atualizar no BD
    Consulta converterParaModel(Long id, ConsultaRequestDto dto) {
        return new Consulta(
                id,
                dto.dataHora(),
                dto.status(),
                dto.observacoes(),
                buscarMedicoPorId(dto.idMedico()),
                buscarPacientePorId(dto.idPaciente())
        );
    }

    ConsultaResponseDto converterParaResponseDto(Consulta model) {
        return new ConsultaResponseDto(
                model.getId(),
                model.getData_hora(),
                model.getStatus(),
                model.getObservacoes(),
                model.getMedico().getId(),
                model.getMedico().getNome(),
                formatarCrm(model.getMedico().getCrm()),
                model.getPaciente().getId(),
                model.getPaciente().getNome()
        );
    }

    // MÉTODOS DE CONSULTA DE OUTRA ENTIDADE ---------------------------------------------------------------------------

    private Medico buscarMedicoPorId(Long id) {
        return medicoRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado médico com o id " + id)
        );
    }

    private Paciente buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrado paciente com o id " + id)
        );
    }
}
