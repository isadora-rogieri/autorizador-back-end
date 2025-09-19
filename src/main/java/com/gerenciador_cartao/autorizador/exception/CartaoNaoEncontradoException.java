package com.gerenciador_cartao.autorizador.exception;

public class CartaoNaoEncontradoException extends RuntimeException{
    public CartaoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}