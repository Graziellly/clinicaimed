package com.clinicaimed.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.clinicaimed.entity.Usuario;
import com.clinicaimed.repository.UsuarioRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DataLoader(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("DATA LOADER EXECUTANDO");

        usuarioRepository.save(
            new Usuario("Maria Recepção", "recepcao@clinicaimed.com", "123", "RECEPCAO")
        );

        usuarioRepository.save(
            new Usuario("Dr. Carlos Lima", "medico@clinicaimed.com", "123", "MEDICO")
        );

        System.out.println("USUÁRIOS CRIADOS");
    }
}