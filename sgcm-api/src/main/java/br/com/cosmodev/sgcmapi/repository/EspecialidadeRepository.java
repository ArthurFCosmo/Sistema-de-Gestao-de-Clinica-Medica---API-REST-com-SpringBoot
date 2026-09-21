package br.com.cosmodev.sgcmapi.repository;

import br.com.cosmodev.sgcmapi.model.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
}
