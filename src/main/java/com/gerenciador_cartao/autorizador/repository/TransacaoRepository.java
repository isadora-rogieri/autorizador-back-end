package com.gerenciador_cartao.autorizador.repository;

import com.gerenciador_cartao.autorizador.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
}
