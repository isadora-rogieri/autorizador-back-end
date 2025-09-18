package com.gerenciador_cartao.autorizador.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name = "ID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cartao_id", nullable = false)
    private Cartao cartao;

    @Column( name = "DATA_HORA")
    private LocalDateTime dataTransacao;

    @Column( name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column( name = "VALOR")
    private BigDecimal valor;

    public Transacao(Cartao cartao, Status status, BigDecimal valor) {
        this.cartao = cartao;
        this.dataTransacao = LocalDateTime.now();
        this.status = status;
        this.valor = valor;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
