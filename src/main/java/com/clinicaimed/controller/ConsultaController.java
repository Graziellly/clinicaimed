package com.clinicaimed.controller;

import com.clinicaimed.entity.Consulta;
import com.clinicaimed.entity.Medico;
import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.MedicoRepository;
import com.clinicaimed.repository.PacienteRepository;
import com.clinicaimed.service.ConsultaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConsultaController {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaService consultaService;

    public ConsultaController(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            ConsultaService consultaService
    ) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultaService = consultaService;
    }

    @GetMapping("/api/horarios-disponiveis")
    @ResponseBody
    public java.util.List<String> horariosDisponiveis(
            @RequestParam String medico,
            @RequestParam String data
    ) {
        return consultaService.buscarHorariosDisponiveis(medico, data);
    }

    @GetMapping("/consultas")
    public String listarConsultas(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");

        if (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO")) {
            return "redirect:/dashboard-medico";
        }

        model.addAttribute("nomeUsuario", session.getAttribute("usuarioLogado"));
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("consultas", consultaRepository.findAll());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        model.addAttribute("editando", false);

        return "consultas";
    }

    @PostMapping("/consultas/salvar")
    public String salvarConsulta(@ModelAttribute Consulta consulta, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");

        if (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO")) {
            return "redirect:/dashboard-medico";
        }

        consultaRepository.save(consulta);
        return "redirect:/consultas";
    }

    @GetMapping("/consultas/editar/{id}")
    public String editarConsulta(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");

        if (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO")) {
            return "redirect:/dashboard-medico";
        }

        Consulta consulta = consultaRepository.findById(id).orElseThrow();

        model.addAttribute("nomeUsuario", session.getAttribute("usuarioLogado"));
        model.addAttribute("consulta", consulta);
        model.addAttribute("consultas", consultaRepository.findAll());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        model.addAttribute("editando", true);

        return "consultas";
    }

    @GetMapping("/consultas/finalizar/{id}")
    public String finalizarConsulta(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");
        if (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO")) {
            return "redirect:/login";
        }

        consultaService.finalizarConsulta(id);
        return "redirect:/consultas";
    }

    @GetMapping("/consultas/cancelar/{id}")
    public String cancelarConsulta(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");
        if (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO")) {
            return "redirect:/login";
        }

        consultaService.cancelarConsulta(id);
        return "redirect:/consultas";
    }

    @GetMapping("/consultas/excluir/{id}")
    public String excluirConsulta(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");

        if (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO")) {
            return "redirect:/dashboard-medico";
        }

        consultaRepository.deleteById(id);
        return "redirect:/consultas";
    }

    @GetMapping("/minhas-consultas")
    public String minhasConsultas(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        String perfil = (String) session.getAttribute("perfil");

        if (perfil == null || !perfil.equalsIgnoreCase("MEDICO")) {
            return "redirect:/login";
        }

        String email = (String) session.getAttribute("emailUsuario");

        if (email == null || email.isBlank()) {
            model.addAttribute("erro", "E-mail do usuário não encontrado na sessão.");
            model.addAttribute("nomeUsuario", session.getAttribute("usuarioLogado"));
            model.addAttribute("consultas", java.util.Collections.emptyList());
            return "consultas-medico";
        }

        Medico medico = medicoRepository.findAll()
        .stream()
        .filter(m -> m.getEmail() != null && m.getEmail().equalsIgnoreCase(email))
        .findFirst()
        .orElse(null);

if (medico == null) {
    model.addAttribute("erro", "Nenhum médico cadastrado com o e-mail: " + email);
    model.addAttribute("nomeUsuario", session.getAttribute("usuarioLogado"));
    model.addAttribute("consultas", java.util.Collections.emptyList());
    return "consultas-medico";
}

        model.addAttribute("nomeUsuario", session.getAttribute("usuarioLogado"));
        model.addAttribute("consultas", consultaRepository.findByMedico(medico));

        return "consultas-medico";
    }
}