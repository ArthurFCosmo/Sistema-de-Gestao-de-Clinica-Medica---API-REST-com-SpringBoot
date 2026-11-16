package br.com.cosmodev.sgcmapi.repository;

import br.com.cosmodev.sgcmapi.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
