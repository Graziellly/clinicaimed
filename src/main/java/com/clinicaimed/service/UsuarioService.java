package com.clinicaimed.service;

import com.clinicaimed.entity.Medico;
import com.clinicaimed.entity.Usuario;
import com.clinicaimed.repository.MedicoRepository;
import com.clinicaimed.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, MedicoRepository medicoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
    }

    // 🔍 Verifica se e-mail já existe
    public boolean emailJaExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // 👤 Cadastro de usuário
    public void cadastrarUsuario(Usuario usuario) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 🔐 Criptografar senha
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        usuarioRepository.save(usuario);

        // 👨‍⚕️ Se for médico, cria também na tabela medico
        if ("MEDICO".equalsIgnoreCase(usuario.getPerfil())) {

            boolean medicoJaExiste = medicoRepository.findByEmail(usuario.getEmail()).isPresent();

            if (!medicoJaExiste) {
                Medico medico = new Medico();
                medico.setNome(usuario.getNome());
                medico.setEmail(usuario.getEmail());
                medico.setCrm("");
                medico.setEspecialidade("");

                medicoRepository.save(medico);
            }
        }
    }

    // 🔐 Login seguro
    public Optional<Usuario> autenticar(String email, String senha) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // 🔥 comparação segura
            if (encoder.matches(senha, usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }
}