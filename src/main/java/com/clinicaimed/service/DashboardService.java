package com.clinicaimed.service;

import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.MedicoRepository;
import com.clinicaimed.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;

    public DashboardService(
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            ConsultaRepository consultaRepository
    ) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaRepository = consultaRepository;
    }

    public long totalPacientes() {
        return pacienteRepository.count();
    }

    public long totalMedicos() {
        return medicoRepository.count();
    }

    public long totalConsultas() {
        return consultaRepository.count();
    }

    public long consultasHoje() {
        return consultaRepository.findAll()
                .stream()
                .filter(c -> c.getData() != null && c.getData().equals(LocalDate.now()))
                .count();
    }

    public long solicitacoesPendentes() {
        return 0;
    }
}