package com.gerenciador_cartao.autorizador.service;

import com.gerenciador_cartao.autorizador.dto.TransacaoDto;
import com.gerenciador_cartao.autorizador.exception.TransacaoException;
import com.gerenciador_cartao.autorizador.model.Status;
import com.gerenciador_cartao.autorizador.model.Transacao;
import com.gerenciador_cartao.autorizador.repository.TransacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransacaoService {

    private static final Logger logger = LoggerFactory.getLogger(TransacaoService.class);

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private CartaoService cartaoService;

    public TransacaoDto realizaTransacao(TransacaoDto dto) {
        var transacao = cartaoService.debitar(dto.getNumeroCartao(), dto.getSenha(), dto.getValor());
        Transacao transacaoSave = new Transacao(
                transacao.getRight(),
                transacao.getLeft(),
                dto.getValor()
        );

        var transacaoDto = toDto(repository.save(transacaoSave));

        if (!transacao.getLeft().equals(Status.SUCESSO)) {
            logger.error("ERRO AO REALIZAR TRANSACAO: " + dto.getNumeroCartao() + " - " + transacao.getLeft());
            throw new TransacaoException(transacao.getLeft());
        }
        logger.info("TRANSACAO REALIZADA COM SUCESSO: " + dto.getNumeroCartao());
        return transacaoDto;
    }

    private TransacaoDto toDto(Transacao entity) {
        TransacaoDto transacaoDto = new TransacaoDto();
        transacaoDto.setDataTransacao(entity.getDataTransacao());
        transacaoDto.setNumeroCartao(entity.getCartao().getNumeroCartao());
        transacaoDto.setValor(entity.getValor());
        transacaoDto.setStatus(entity.getStatus());
        return transacaoDto;
    }

}
