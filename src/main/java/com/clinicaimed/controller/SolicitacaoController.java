package com.clinicaimed.controller;

import com.clinicaimed.entity.Consulta;
import com.clinicaimed.entity.Medico;
import com.clinicaimed.entity.Paciente;
import com.clinicaimed.entity.Solicitacao;
import com.clinicaimed.repository.ConsultaRepository;
import com.clinicaimed.repository.MedicoRepository;
import com.clinicaimed.repository.PacienteRepository;
import com.clinicaimed.repository.SolicitacaoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class SolicitacaoController {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public SolicitacaoController(
            SolicitacaoRepository solicitacaoRepository,
            ConsultaRepository consultaRepository,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository
    ) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping("/solicitacoes")
    public String listarSolicitacoes(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        model.addAttribute("solicitacao", new Solicitacao());
        model.addAttribute("solicitacoes", solicitacaoRepository.findAll());
        model.addAttribute("editando", false);

        return "solicitacoes";
    }

    @PostMapping("/solicitacoes/salvar")
    public String salvarSolicitacao(@ModelAttribute Solicitacao solicitacao, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        if (solicitacao.getStatus() == null || solicitacao.getStatus().isBlank()) {
            solicitacao.setStatus("Pendente");
        }

        solicitacaoRepository.save(solicitacao);
        return "redirect:/solicitacoes";
    }

    @GetMapping("/solicitacoes/editar/{id}")
    public String editarSolicitacao(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Solicitacao solicitacao = solicitacaoRepository.findById(id).orElseThrow();

        model.addAttribute("solicitacao", solicitacao);
        model.addAttribute("solicitacoes", solicitacaoRepository.findAll());
        model.addAttribute("editando", true);

        return "solicitacoes";
    }

    @GetMapping("/solicitacoes/excluir/{id}")
    public String excluirSolicitacao(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        solicitacaoRepository.deleteById(id);
        return "redirect:/solicitacoes";
    }

    @PostMapping("/agendamento/enviar")
    public String enviarSolicitacaoPublica(@ModelAttribute Solicitacao solicitacao) {
        solicitacao.setStatus("Pendente");
        solicitacaoRepository.save(solicitacao);
        return "redirect:/?sucesso";
    }

    @GetMapping("/solicitacao/confirmar/{id}")
    public String confirmarSolicitacao(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        Solicitacao s = solicitacaoRepository.findById(id).orElseThrow();

        if ("Confirmada".equalsIgnoreCase(s.getStatus())) {
            return "redirect:/solicitacoes";
        }

        Medico medico = medicoRepository.findAll()
                .stream()
                .filter(m -> m.getNome().equalsIgnoreCase(s.getMedico()))
                .findFirst()
                .orElse(null);

        if (medico == null) {
            return "redirect:/solicitacoes";
        }

        LocalTime horaConsulta = s.getHora();

        if (s.getDataConsulta() != null && horaConsulta != null) {
        boolean horarioOcupado = consultaRepository.existsByMedicoAndDataAndHora(
            medico,
            s.getDataConsulta(),
            horaConsulta
        );

        if (horarioOcupado) {
        return "redirect:/solicitacoes";
        }
}

        s.setStatus("Confirmada");
        solicitacaoRepository.save(s);

        Paciente paciente = new Paciente();
        paciente.setNome(s.getNome());
        paciente.setTelefone(s.getTelefone());
        paciente.setEmail(s.getEmail());
        pacienteRepository.save(paciente);

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setData(s.getDataConsulta());
        consulta.setHora(horaConsulta);
        consulta.setStatus("Agendada");
        consultaRepository.save(consulta);

        String numero = s.getTelefone() != null
                ? s.getTelefone().replaceAll("[^0-9]", "")
                : "";

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = s.getDataConsulta() != null
                ? s.getDataConsulta().format(formatoData)
                : "data não informada";

        String horaFormatada = s.getHora() != null
        ? s.getHora().toString()
        : "horário não informado";

        String mensagem = "Olá " + s.getNome() + "\n\n"
                + "Sua consulta foi confirmada com sucesso.\n\n"
                + "Data: " + dataFormatada + "\n"
                + "Hora: " + horaFormatada + "\n"
                + "Médico: " + s.getMedico() + "\n\n"
                + "Clínica Imed";

        if (numero.isBlank()) {
            return "redirect:/solicitacoes";
        }

        String url = "https://wa.me/55" + numero + "?text="
                + URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

        return "redirect:" + url;
    }
}