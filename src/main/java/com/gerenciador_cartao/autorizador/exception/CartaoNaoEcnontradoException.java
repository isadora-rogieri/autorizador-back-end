package com.gerenciador_cartao.autorizador.exception;

public class CartaoNaoEcnontradoException extends RuntimeException{
    public CartaoNaoEcnontradoException(String mensagem) {
        super(mensagem);
    }
}