package br.com.cosmodev.sgcmapi.repository;

import br.com.cosmodev.sgcmapi.enums.StatusConsulta;
import br.com.cosmodev.sgcmapi.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

     boolean existsByMedico_IdAndStatusIn(Long idMedico, Collection<StatusConsulta> status);

     boolean existsByPaciente_IdAndStatusIn(Long idPaciente, Collection<StatusConsulta> status);

     List<Consulta> findByMedico_IdAndDataHoraBetween(Long idMedico, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim);

}
