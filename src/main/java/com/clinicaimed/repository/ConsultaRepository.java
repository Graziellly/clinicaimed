package com.clinicaimed.repository;

import com.clinicaimed.entity.Consulta;
import com.clinicaimed.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByMedico(Medico medico);

    boolean existsByMedicoAndDataAndHora(Medico medico, LocalDate data, LocalTime hora);

    boolean existsByPaciente_Id(Long pacienteId);

    boolean existsByMedico_Id(Long medicoId);
}