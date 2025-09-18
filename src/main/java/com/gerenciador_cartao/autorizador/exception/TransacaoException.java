package com.gerenciador_cartao.autorizador.exception;

import com.gerenciador_cartao.autorizador.model.Status;

public class TransacaoException extends RuntimeException {

    public TransacaoException(Status status) {
        super(status.toString());
    }
}