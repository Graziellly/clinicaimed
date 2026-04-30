package com.clinicaimed.controller;

import com.clinicaimed.entity.Paciente;
import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.PacienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;

    public PacienteController(PacienteRepository pacienteRepository,
                              ConsultaRepository consultaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.consultaRepository = consultaRepository;
    }

    @GetMapping("/pacientes")
    public String listarPacientes(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        model.addAttribute("paciente", new Paciente());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("editando", false);

        return "pacientes";
    }

    @PostMapping("/pacientes/salvar")
    public String salvarPaciente(@ModelAttribute Paciente paciente, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        pacienteRepository.save(paciente);
        return "redirect:/pacientes";
    }

    @GetMapping("/pacientes/editar/{id}")
    public String editarPaciente(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Paciente paciente = pacienteRepository.findById(id).orElseThrow();

        model.addAttribute("paciente", paciente);
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("editando", true);

        return "pacientes";
    }

    @GetMapping("/pacientes/excluir/{id}")
    public String excluirPaciente(@PathVariable Long id, HttpSession session) {
    if (session.getAttribute("usuarioLogado") == null) {
        return "redirect:/login";
    }

    if (consultaRepository.existsByPaciente_Id(id)) {
        return "redirect:/pacientes?erro=Paciente possui consulta cadastrada";
    }

    pacienteRepository.deleteById(id);
    return "redirect:/pacientes";
}
}