package com.gerenciador_cartao.autorizador.repository;

import com.gerenciador_cartao.autorizador.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, UUID> {
    boolean existsByNumeroCartao(String numeroCartao);

    Cartao findByNumeroCartao(String numeroCartao);
}
