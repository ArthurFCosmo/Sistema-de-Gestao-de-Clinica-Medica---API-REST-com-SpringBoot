package br.com.cosmodev.sgcmapi.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.cosmodev.sgcmapi.model.Consulta;

public class ConsultaSpecs {

    // Construtor privado para evitar que a classe seja instanciada
    private ConsultaSpecs() {}

    public static Specification<Consulta> comMedico(Long medicoId) {
        return (root, query, cb) -> medicoId == null ? null : cb.equal(root.get("medico").get("id"), medicoId);
    }

    public static Specification<Consulta> comPaciente(Long pacienteId) {
        return (root, query, cb) -> pacienteId == null ? null : cb.equal(root.get("paciente").get("id"), pacienteId);
    }

}
