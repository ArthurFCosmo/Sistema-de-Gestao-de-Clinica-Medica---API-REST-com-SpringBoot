package br.com.cosmodev.sgcmapi.specification;

import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.model.Consulta;
import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.model.Paciente;
import br.com.cosmodev.sgcmapi.repository.ConsultaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ConsultaSpecsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Test
    void deveRetornarConsultasDoMedicoComPaginacaoEOrdenacao() {
        Especialidade especialidade = salvarEspecialidade();
        Medico medicoAlvo = salvarMedico("111111-PE", "alvo@email.com", especialidade);
        Medico outroMedico = salvarMedico("222222-PE", "outro@email.com", especialidade);
        Paciente paciente = salvarPaciente();
        salvarConsulta(medicoAlvo, paciente, LocalDateTime.of(2030, 5, 10, 14, 30));
        salvarConsulta(medicoAlvo, paciente, LocalDateTime.of(2030, 5, 11, 14, 30));
        salvarConsulta(medicoAlvo, paciente, LocalDateTime.of(2030, 5, 12, 14, 30));
        salvarConsulta(outroMedico, paciente, LocalDateTime.of(2030, 5, 13, 14, 30));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("dataHora"));

        // EXECUÇÃO
        Page<Consulta> pagina = consultaRepository.findAll(ConsultaSpecs.comMedico(medicoAlvo.getId()), pageable);

        // CONFIRMAÇÃO
        assertAll(
                () -> assertEquals(3, pagina.getTotalElements()),
                () -> assertEquals(2, pagina.getTotalPages()),
                () -> assertEquals(2, pagina.getContent().size()),
                () -> assertTrue(pagina.getContent().stream()
                        .allMatch(consulta -> consulta.getMedico().getId().equals(medicoAlvo.getId()))),
                () -> assertEquals(LocalDateTime.of(2030, 5, 10, 14, 30), pagina.getContent().get(0).getDataHora()),
                () -> assertEquals(LocalDateTime.of(2030, 5, 11, 14, 30), pagina.getContent().get(1).getDataHora())
        );
    }

    @Test
    void deveRetornarTodasAsConsultasQuandoIdDoMedicoForNulo() {
        Especialidade especialidade = salvarEspecialidade();
        Medico medico = salvarMedico("111111-PE", "medico@email.com", especialidade);
        Paciente paciente = salvarPaciente();
        salvarConsulta(medico, paciente, LocalDateTime.of(2030, 5, 10, 14, 30));
        salvarConsulta(medico, paciente, LocalDateTime.of(2030, 5, 11, 14, 30));

        List<Consulta> resultado = consultaRepository.findAll(ConsultaSpecs.comMedico(null));

        assertEquals(2, resultado.size());
    }

    @Test
    void deveRetornarListaVaziaQuandoMedicoNaoPossuirConsultas() {
        Especialidade especialidade = salvarEspecialidade();
        Medico medicoComConsulta = salvarMedico("111111-PE", "medico@email.com", especialidade);
        Medico medicoSemConsulta = salvarMedico("222222-PE", "outro@email.com", especialidade);
        Paciente paciente = salvarPaciente();
        salvarConsulta(medicoComConsulta, paciente, LocalDateTime.of(2030, 5, 10, 14, 30));

        List<Consulta> resultado = consultaRepository.findAll(ConsultaSpecs.comMedico(medicoSemConsulta.getId()));

        assertTrue(resultado.isEmpty());
    }

    // HELPERS PARA ELIMINAR BOILERPLATE --------------------------------------------------------------------------------------------------

    private Especialidade salvarEspecialidade() {
        return entityManager.persistAndFlush(new Especialidade(null, "Clínica geral", null));
    }

    private Medico salvarMedico(String crm, String email, Especialidade especialidade) {
        return entityManager.persistAndFlush(
                new Medico(null, "Dr. " + crm, crm, email, "11999999999", true, especialidade)
        );
    }

    private Paciente salvarPaciente() {
        return entityManager.persistAndFlush(
                new Paciente(null, "Ana Silva", "12345678901", "paciente@email.com", "11988887777",
                        LocalDate.of(1990, 1, 1), true)
        );
    }

    private Consulta salvarConsulta(Medico medico, Paciente paciente, LocalDateTime dataHora) {
        return entityManager.persistAndFlush(
                new Consulta(null, dataHora, StatusConsulta.AGENDADA, null, medico, paciente)
        );
    }
}
