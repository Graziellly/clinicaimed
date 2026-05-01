package com.clinicaimed.repository;

import com.clinicaimed.entity.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    long countByStatus(String status);

}