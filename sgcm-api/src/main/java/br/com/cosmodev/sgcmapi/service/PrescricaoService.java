package br.com.cosmodev.sgcmapi.service;

import br.com.cosmodev.sgcmapi.dtos.PrescricaoRequestDto;
import br.com.cosmodev.sgcmapi.dtos.PrescricaoResponseDto;
import br.com.cosmodev.sgcmapi.exceptions.ElementoNaoEncontradoException;
import br.com.cosmodev.sgcmapi.model.Consulta;
import br.com.cosmodev.sgcmapi.model.Prescricao;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import br.com.cosmodev.sgcmapi.repository.PrescricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static br.com.cosmodev.sgcmapi.service.UtilsSgcm.formatarCrm;

@Service
@RequiredArgsConstructor
public class PrescricaoService {

    // INJEÇÃO DE DEPENDÊNCIAS -----------------------------------------------------------------------------------------

    private final ConsultaRepository consultaRepository;
    private final PrescricaoRepository prescricaoRepository;

    // MÉTODOS PADRÃO DO CRUD ------------------------------------------------------------------------------------------

    public PrescricaoResponseDto salvarPrescricao(PrescricaoRequestDto dto) {
        return converterParaResponseDto(prescricaoRepository.save(converterParaModel(dto)));

        // todo: Não pode ser criada ou atualizada se a consulta vinculada não tiver marcada como REALIZADA
    }

    public PrescricaoResponseDto buscarPrescricaoPeloId(Long id) {
        return converterParaResponseDto(prescricaoRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada prescrição com id " + id))
        );
    }

    public List<PrescricaoResponseDto> listarPrescricoes() {
        return prescricaoRepository.findAll().stream().map(this::converterParaResponseDto).toList();
    }

    public void deletarPrescricaoPeloId(Long id) {
        prescricaoRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada prescrição com id " + id)
        );

        prescricaoRepository.deleteById(id);
    }

    public PrescricaoResponseDto atualizarPrescricao(Long id, PrescricaoRequestDto dto, LocalDate dataEmissao) {
        prescricaoRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada prescrição com id " + id)
        );

        // todo: Não pode ser criada ou atualizada se a consulta vinculada não tiver marcada como REALIZADA

        return converterParaResponseDto(prescricaoRepository.save(converterParaModel(id, dto, dataEmissao)));
    }

    // MÉTODOS DE CONVERSÃO DE ENTIDADES -------------------------------------------------------------------------------

    Prescricao converterParaModel(PrescricaoRequestDto dto) {
        return new Prescricao(
                null,
                dto.descricao(),
                dto.medicamentos(),
                LocalDate.now(),
                buscarConsultaPorId(dto.idConsulta())
        );
    }

    Prescricao converterParaModel(Long id, PrescricaoRequestDto dto, LocalDate dataEmissao) {
        return new Prescricao(
                id,
                dto.descricao(),
                dto.medicamentos(),
                dataEmissao,
                buscarConsultaPorId(dto.idConsulta())
        );
    }

    PrescricaoResponseDto converterParaResponseDto(Prescricao model) {
        return new PrescricaoResponseDto(
                model.getId(),
                model.getConsulta().getId(),
                model.getConsulta().getMedico().getNome(),
                formatarCrm(model.getConsulta().getMedico().getCrm()),
                model.getConsulta().getPaciente().getNome(),
                model.getConsulta().getData_hora(),
                model.getDescricao(),
                model.getMedicamentos(),
                model.getData_emissao()
        );
    }

    // MÉTODOS DE CONSULTA DE OUTRA ENTIDADE ---------------------------------------------------------------------------

    private Consulta buscarConsultaPorId(Long id) {
        return consultaRepository.findById(id).orElseThrow(
                () -> new ElementoNaoEncontradoException("Não foi encontrada consulta com id " + id)
        );
    }
}
