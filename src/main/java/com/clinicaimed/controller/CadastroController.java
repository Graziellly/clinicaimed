package com.clinicaimed.controller;

import com.clinicaimed.entity.Medico;
import com.clinicaimed.entity.Usuario;
import com.clinicaimed.repository.MedicoRepository;
import com.clinicaimed.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CadastroController {

    private final UsuarioService usuarioService;
    private final MedicoRepository medicoRepository;

    public CadastroController(UsuarioService usuarioService, MedicoRepository medicoRepository) {
        this.usuarioService = usuarioService;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping("/cadastro")
    public String abrirCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarUsuario(
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String crm,
            @RequestParam(required = false) String especialidade,
            Model model
    ) {

        if (usuarioService.emailJaExiste(usuario.getEmail())) {
            model.addAttribute("erro", "Já existe uma conta com esse e-mail.");
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }

        if ("MEDICO".equalsIgnoreCase(usuario.getPerfil())) {
            if (crm == null || crm.isBlank() || especialidade == null || especialidade.isBlank()) {
                model.addAttribute("erro", "Para perfil Médico, informe CRM e especialidade.");
                model.addAttribute("usuario", usuario);
                return "cadastro";
            }
        }

        usuarioService.cadastrarUsuario(usuario);

        if ("MEDICO".equalsIgnoreCase(usuario.getPerfil())) {
            Medico medico = new Medico();
            medico.setNome(usuario.getNome());
            medico.setEmail(usuario.getEmail());
            medico.setCrm(crm);
            medico.setEspecialidade(especialidade);
            medicoRepository.save(medico);
        }

        return "redirect:/login";
    }
}