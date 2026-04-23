package com.clinicaimed.controller;

import com.clinicaimed.entity.Usuario;
import com.clinicaimed.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String abrirLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogado") != null) {
            String perfil = (String) session.getAttribute("perfil");

            if ("RECEPCAO".equalsIgnoreCase(perfil)) {
                return "redirect:/dashboard-recepcao";
            }

            if ("MEDICO".equalsIgnoreCase(perfil)) {
                return "redirect:/dashboard-medico";
            }
        }

        return "login";
    }

    @PostMapping("/login")
    public String fazerLogin(@RequestParam String email,
                             @RequestParam String senha,
                             HttpSession session,
                             Model model) {

        Optional<Usuario> usuarioOpt = usuarioService.autenticar(email, senha);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioLogado", usuario.getNome());
            session.setAttribute("perfil", usuario.getPerfil());
            session.setAttribute("emailUsuario", usuario.getEmail());

            if ("RECEPCAO".equalsIgnoreCase(usuario.getPerfil())) {
                return "redirect:/dashboard-recepcao";
            }

            if ("MEDICO".equalsIgnoreCase(usuario.getPerfil())) {
                return "redirect:/dashboard-medico";
            }

            model.addAttribute("erro", "Perfil inválido.");
            return "login";
        }

        model.addAttribute("erro", "E-mail ou senha inválidos.");
        return "login";
    }

    @GetMapping("/logout")
    public String sair(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}