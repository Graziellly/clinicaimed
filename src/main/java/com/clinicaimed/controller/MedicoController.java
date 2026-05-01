package com.clinicaimed.controller;

import com.clinicaimed.entity.Medico;
import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.MedicoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MedicoController {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    

    public MedicoController(MedicoRepository medicoRepository,
                        ConsultaRepository consultaRepository) {
    this.medicoRepository = medicoRepository;
    this.consultaRepository = consultaRepository;
}

    @GetMapping("/medicos")
    public String listarMedicos(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        model.addAttribute("medico", new Medico());
        model.addAttribute("medicos", medicoRepository.findAll());
        model.addAttribute("editando", false);
        return "medicos";
    }

    @PostMapping("/medicos/salvar")
    public String salvarMedico(@ModelAttribute Medico medico, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        medicoRepository.save(medico);
        return "redirect:/medicos";
    }

    @GetMapping("/medicos/editar/{id}")
    public String editarMedico(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Medico medico = medicoRepository.findById(id).orElseThrow();
        model.addAttribute("medico", medico);
        model.addAttribute("medicos", medicoRepository.findAll());
        model.addAttribute("editando", true);
        return "medicos";
    }

    @GetMapping("/medicos/excluir/{id}")
    public String excluirMedico(@PathVariable Long id, HttpSession session) {

    if (session.getAttribute("usuarioLogado") == null) {
        return "redirect:/login";
    }

    if (consultaRepository.existsByMedico_Id(id)) {
        return "redirect:/medicos?erro=Medico possui consultas cadastradas";
    }

    medicoRepository.deleteById(id);
    return "redirect:/medicos";
}

    
}
