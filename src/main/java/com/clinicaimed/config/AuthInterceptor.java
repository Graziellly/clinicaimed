package com.clinicaimed.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();

        // Usuário não logado
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect("/login");
            return false;
        }

        String perfil = (String) session.getAttribute("perfil");

        // Área exclusiva da recepção
        if ((uri.startsWith("/dashboard-recepcao")
                || uri.startsWith("/pacientes")
                || uri.startsWith("/medicos")
                || uri.startsWith("/consultas")
                || uri.startsWith("/solicitacoes"))
                && (perfil == null || !perfil.equalsIgnoreCase("RECEPCAO"))) {
            response.sendRedirect("/acesso-negado");
            return false;
        }

        // Área exclusiva do médico
        if ((uri.startsWith("/dashboard-medico")
                || uri.startsWith("/minhas-consultas"))
                && (perfil == null || !perfil.equalsIgnoreCase("MEDICO"))) {
            response.sendRedirect("/acesso-negado");
            return false;
        }

        return true;
    }
}