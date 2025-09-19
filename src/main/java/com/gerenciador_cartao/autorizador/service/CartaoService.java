package com.gerenciador_cartao.autorizador.service;

import com.gerenciador_cartao.autorizador.exception.CartaoExistenteException;
import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.exception.CartaoNaoEncontradoException;
import com.gerenciador_cartao.autorizador.model.Cartao;
import com.gerenciador_cartao.autorizador.model.Status;
import com.gerenciador_cartao.autorizador.repository.CartaoRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Service
public class CartaoService {

    private static final Logger logger = LoggerFactory.getLogger(CartaoService.class);
    @Autowired
    private CartaoRepository repository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Cartao cadastrarCartao(CartaoDto cartaoDto) {
        if (validaCartaoExistente(cartaoDto.getNumeroCartao())) {
            logger.error("JA EXISTE UM CARTAO CADASTRADO COM ESSE NUMERO: " + cartaoDto.getNumeroCartao());
            throw new CartaoExistenteException("Já existe um Cartão cadastrado com esse número");
        }
        Cartao cartao = new Cartao(cartaoDto.getNumeroCartao(),
                passwordEncoder.encode(cartaoDto.getSenha()));
        return repository.save(cartao);
    }

    public BigDecimal consultaSaldoCartao(String numeroCartao) {
        return findCartaoByNumeroCartao(numeroCartao).getSaldo();
    }

    public Cartao findCartaoByNumeroCartao(String numeroCartao) {
        var cartao = repository.findByNumeroCartao(numeroCartao);
        if (isNull(cartao)) {
            logger.error("CARTAO NAO ENCONTRADO " + numeroCartao);
            throw new CartaoNaoEncontradoException("Cartão não encontrado");
        }
        return cartao;
    }

    public Pair<Status, Cartao> debitar(String numeroCartao, String senha, BigDecimal valor) {
        Cartao cartao = findCartaoByNumeroCartao(numeroCartao);

        if (!passwordEncoder.matches(senha, cartao.getSenha())) {
            return Pair.of(Status.SENHA_INVALIDA, cartao);
        }

        if (valor.compareTo(cartao.getSaldo()) > 0) {
            return Pair.of(Status.SALDO_INSUFICIENTE, cartao);
        }

        cartao.debitar(valor);
        return Pair.of(Status.SUCESSO, cartao);
    }

    public CartaoDto toDto(Cartao entity) {
        CartaoDto cartaoDto = new CartaoDto();
        cartaoDto.setNumeroCartao(entity.getNumeroCartao());
        cartaoDto.setSaldo(entity.getSaldo());
        return cartaoDto;
    }

    private boolean validaCartaoExistente(String numeroCartao) {
        return repository.existsByNumeroCartao(numeroCartao);
    }

}