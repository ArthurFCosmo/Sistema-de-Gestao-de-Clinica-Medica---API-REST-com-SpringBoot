package br.com.cosmodev.sgcmapi.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.cosmodev.sgcmapi.model.Medico;

public class MedicoSpecs {

    // Construtor privado para evitar que a classe seja instanciada
    private MedicoSpecs() {}

    // public static Specification<Medico> comId(Long id) {

    //     return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);

    // }

    public static Specification<Medico> comEspecialidade(Long especialidadeId) {
        
        return (root, query, cb) -> especialidadeId == null ? null : cb.equal(root.get("especialidade").get("id"), especialidadeId);

    }

}
