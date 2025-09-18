package com.gerenciador_cartao.autorizador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CartaoDto {

    @NotBlank
    @Size(min = 16, max = 16, message = "O número do cartão deve conter 16 digitos")
    @Pattern(regexp = "\\d+", message = "O número do cartão deve conter apenas dígitos")
    private String numeroCartao;

    @NotBlank
    private String senha;

    private BigDecimal saldo;

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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
