package com.clinicaimed.service;

import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.MedicoRepository;
import com.clinicaimed.repository.PacienteRepository;
import com.clinicaimed.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public DashboardService(
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            ConsultaRepository consultaRepository,
            SolicitacaoRepository solicitacaoRepository
    ) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaRepository = consultaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
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

    // 🔥 MAIS PERFORMÁTICO
    public long consultasHoje() {
        return consultaRepository.countByData(LocalDate.now());
    }

    // 🔥 AGORA FUNCIONA DE VERDADE
    public long solicitacoesPendentes() {
        return solicitacaoRepository.countByStatus("Pendente");
    }
}