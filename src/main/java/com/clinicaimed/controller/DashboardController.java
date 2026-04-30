package com.clinicaimed.controller;

import com.clinicaimed.entity.Consulta;
import com.clinicaimed.entity.Medico;
import com.clinicaimed.entity.Solicitacao;
import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.MedicoRepository;
import com.clinicaimed.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;

    public DashboardController(DashboardService dashboardService,
                               MedicoRepository medicoRepository,
                               ConsultaRepository consultaRepository) {
        this.dashboardService = dashboardService;
        this.medicoRepository = medicoRepository;
        this.consultaRepository = consultaRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("solicitacao", new Solicitacao());
        return "index";
    }

    @GetMapping("/dashboard-recepcao")
    public String dashboardRecepcao(HttpSession session, Model model) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");
        if (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO")) {
            return "redirect:/login";
        }

        model.addAttribute("nomeUsuario", session.getAttribute("usuarioLogado"));

        // 🔥 Agora usando SERVICE (PROFISSIONAL)
        model.addAttribute("totalPacientes", dashboardService.totalPacientes());
        model.addAttribute("totalMedicos", dashboardService.totalMedicos());
        model.addAttribute("totalConsultas", dashboardService.totalConsultas());
        model.addAttribute("consultasHoje", dashboardService.consultasHoje());
        model.addAttribute("solicitacoesPendentes", dashboardService.solicitacoesPendentes());

        return "dashboard-recepcao";
    }

    @GetMapping("/dashboard-medico")
    public String dashboardMedico(HttpSession session, Model model) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");
        if (perfil == null || !perfil.equalsIgnoreCase("MEDICO")) {
            return "redirect:/login";
        }

        String email = (String) session.getAttribute("emailUsuario");

        model.addAttribute("nomeUsuario", session.getAttribute("usuarioLogado"));

        if (email == null || email.isBlank()) {
            model.addAttribute("consultas", Collections.emptyList());
            model.addAttribute("consultasHoje", 0);
            model.addAttribute("totalConsultas", 0);
            model.addAttribute("proximasConsultas", 0);
            return "dashboard-medico";
        }

       Medico medico = medicoRepository.findAll()
        .stream()
        .filter(m -> m.getEmail() != null && m.getEmail().equalsIgnoreCase(email))
        .findFirst()
        .orElse(null);

if (medico == null) {
    model.addAttribute("consultas", Collections.emptyList());
    model.addAttribute("consultasHoje", 0);
    model.addAttribute("totalConsultas", 0);
    model.addAttribute("proximasConsultas", 0);
    return "dashboard-medico";
}

        List<Consulta> consultas = consultaRepository.findByMedico(medico);

        long consultasHoje = consultas.stream()
                .filter(c -> c.getData() != null && c.getData().equals(LocalDate.now()))
                .count();

        long proximasConsultas = consultas.stream()
                .filter(c -> c.getData() != null && !c.getData().isBefore(LocalDate.now()))
                .count();

        model.addAttribute("consultas", consultas);
        model.addAttribute("consultasHoje", consultasHoje);
        model.addAttribute("totalConsultas", consultas.size());
        model.addAttribute("proximasConsultas", proximasConsultas);

        return "dashboard-medico";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}