package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.ConsultaRequestDto;
import br.com.cosmodev.sgcmapi.dtos.ConsultaResponseDto;
import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.exceptions.RegraDeNegocioVioladaException;
import br.com.cosmodev.sgcmapi.model.Consulta;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import br.com.cosmodev.sgcmapi.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.cosmodev.sgcmapi.service.UtilsSgcm.FORMATO_DATA_HORA;
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

        // Permitindo que consultas inativas ignorem o filtro de conflito de horario
        if (StatusConsulta.ativos().contains(dto.status())) {

            //Não podem criar (marcar) consultas num intervalo sobrebosto de 20 minutos entre elas
            verificarDisponibilidadeDeHorarioDoMedico(null, dto.idMedico(), dto.dataHora());

        }

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
        if (!consultaRepository.existsById(id)) {
            throw new ElementoNaoEncontradoException("Não foi encontrada consulta com id " + id);
        }

        consultaRepository.deleteById(id);
    }

    public ConsultaResponseDto atualizarConsulta(Long id, ConsultaRequestDto dto) {
        if (!consultaRepository.existsById(id)) {
            throw new ElementoNaoEncontradoException("Não foi encontrada consulta com id " + id);
        }

        // Permitindo que consultas inativas ignorem o filtro de conflito de horario
        if (StatusConsulta.ativos().contains(dto.status())) {

            //Não podem criar (marcar) consultas num intervalo sobrebosto de 20 minutos entre elas
            verificarDisponibilidadeDeHorarioDoMedico(id, dto.idMedico(), dto.dataHora());

        }

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
                model.getDataHora(),
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

    // MÉTODOS DAS REGRAS DE SERVIÇO -----------------------------------------------------------------------------------

    private static final long DURACAO_DA_CONSULTA_EM_MINUTOS = 20L;

    private void verificarDisponibilidadeDeHorarioDoMedico(Long idConsultaAtual,Long idMedico, LocalDateTime dataHoraConsulta) {

        LocalDateTime dataHoraConsultaFim = dataHoraConsulta.plusMinutes(DURACAO_DA_CONSULTA_EM_MINUTOS);

        List<Consulta> consultasProximasDoHorario = consultaRepository.findByMedico_IdAndDataHoraBetween(
                idMedico,
                dataHoraConsulta.minusMinutes(DURACAO_DA_CONSULTA_EM_MINUTOS),
                dataHoraConsultaFim
        );

        for (Consulta consultaExistente : consultasProximasDoHorario) {

            if (consultaExistente.getId().equals(idConsultaAtual)) {
                continue;
            }

            if (!StatusConsulta.ativos().contains(consultaExistente.getStatus())) {
                continue;
            }

            LocalDateTime dataHoraConsultaExistente = consultaExistente.getDataHora();
            LocalDateTime dataHoraConsultaExistenteFim = dataHoraConsultaExistente.plusMinutes(DURACAO_DA_CONSULTA_EM_MINUTOS);

            boolean haSobreposicaoDeHorarios = (
                    dataHoraConsultaExistente.isBefore(dataHoraConsultaFim) && dataHoraConsultaExistenteFim.isAfter(dataHoraConsulta)
                    );

            if (haSobreposicaoDeHorarios) {
                throw new RegraDeNegocioVioladaException(
                        "O médico com id " + idMedico + " já possui consulta de " +
                                dataHoraConsultaExistente.format(FORMATO_DATA_HORA) + " até " +
                                dataHoraConsultaExistenteFim.format(FORMATO_DATA_HORA) +
                                ", o que conflita com o horário solicitado (" + dataHoraConsulta.format(FORMATO_DATA_HORA) + ")."
                );
            }
        }

    }

}
