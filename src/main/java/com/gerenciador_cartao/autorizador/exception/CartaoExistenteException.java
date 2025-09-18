package com.gerenciador_cartao.autorizador.exception;

public class CartaoExistenteException extends RuntimeException{
    public CartaoExistenteException(String mensagem) {
        super(mensagem);
    }
}