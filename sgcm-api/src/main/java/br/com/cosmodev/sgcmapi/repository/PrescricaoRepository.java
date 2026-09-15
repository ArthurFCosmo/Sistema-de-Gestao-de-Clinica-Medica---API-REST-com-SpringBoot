package br.com.cosmodev.sgcmapi.repository;

import br.com.cosmodev.sgcmapi.model.Prescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Long> {
}
