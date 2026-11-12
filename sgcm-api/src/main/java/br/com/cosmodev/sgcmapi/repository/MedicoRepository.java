package br.com.cosmodev.sgcmapi.repository;

import br.com.cosmodev.sgcmapi.model.Medico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long>, JpaSpecificationExecutor<Medico> {

    boolean existsByEspecialidade_Id(Long id);

    List<Medico> findByEspecialidade_Id(Long idEspecialidade);

}
