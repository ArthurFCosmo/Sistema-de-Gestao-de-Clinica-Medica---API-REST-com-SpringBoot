package br.com.cosmodev.sgcmapi.repository;

import br.com.cosmodev.sgcmapi.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {

    Optional<Prescricao> findByConsulta_Id(Long consultaId);

}
