package com.clinicaimed.controller;

import com.clinicaimed.entity.Usuario;
import com.clinicaimed.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CadastroController {

    private final UsuarioService usuarioService;

    public CadastroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cadastro")
    public String abrirCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarUsuario(@ModelAttribute Usuario usuario, Model model) {

        if (usuarioService.emailJaExiste(usuario.getEmail())) {
            model.addAttribute("erro", "Já existe uma conta com esse e-mail.");
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }

        usuarioService.cadastrarUsuario(usuario);

        return "redirect:/login";
    }
}