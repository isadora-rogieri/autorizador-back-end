package com.gerenciador_cartao.autorizador.dto;

import com.gerenciador_cartao.autorizador.model.Status;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoDto {

    @NotBlank(message = "Número do cartão é obrigatório")
    @Size(min = 16, max = 16, message = "O número do cartão deve conter 16 digitos")
    @Pattern(regexp = "\\d+", message = "O número do cartão deve conter apenas dígitos")
    private String numeroCartao;

    @NotBlank(message = "senha é obrigatória")
    @Size(min = 4, max = 4, message = "A senha do cartão deve conter 4 digitos")
    @Pattern(regexp = "\\d+", message = "A senha do cartão deve conter apenas dígitos")
    private String senha;

    @NotNull(message = "valor é obrigatório")
    @Min(value = 1, message = "Valor deve ser maior que um")
    private BigDecimal valor;

    private LocalDateTime dataTransacao;

    private Status status;

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
}
