package com.clinicaimed.service;

import com.clinicaimed.entity.Consulta;
import com.clinicaimed.entity.Medico;
import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;

    public ConsultaService(ConsultaRepository consultaRepository, MedicoRepository medicoRepository) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
    }

    public List<String> buscarHorariosDisponiveis(String medico, String data) {
        String medicoLimpo = medico == null ? "" : medico.trim();

        Medico medicoObj = medicoRepository.findAll()
                .stream()
                .filter(m -> m.getNome() != null && m.getNome().trim().equalsIgnoreCase(medicoLimpo))
                .findFirst()
                .orElse(null);

        if (medicoObj == null) {
            return List.of();
        }

        LocalDate dataConsulta;
        try {
            if (data.contains("/")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataConsulta = LocalDate.parse(data.trim(), formatter);
            } else {
                dataConsulta = LocalDate.parse(data.trim());
            }
        } catch (Exception e) {
            return List.of();
        }

        List<String> horariosBase = List.of(
                "08:00", "09:00", "10:00", "11:00",
                "14:00", "15:00", "16:00", "17:00"
        );

        return horariosBase.stream()
                .filter(h -> !consultaRepository.existsByMedicoAndDataAndHora(
                        medicoObj,
                        dataConsulta,
                        LocalTime.parse(h)
                ))
                .toList();
    }

    public void finalizarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id).orElseThrow();
        consulta.setStatus("Realizada");
        consultaRepository.save(consulta);
    }

    public void cancelarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id).orElseThrow();
        consulta.setStatus("Cancelada");
        consultaRepository.save(consulta);
    }
}