package br.com.cosmodev.sgcmapi.repository;

import br.com.cosmodev.sgcmapi.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
