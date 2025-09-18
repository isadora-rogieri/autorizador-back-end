package com.gerenciador_cartao.autorizador.service;

import com.gerenciador_cartao.autorizador.exception.CartaoExistenteException;
import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.exception.CartaoNaoEcnontradoException;
import com.gerenciador_cartao.autorizador.model.Cartao;
import com.gerenciador_cartao.autorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository repository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CartaoDto cadastrarCartao(CartaoDto cartaoDto) {
        if (validaCartaoExistente(cartaoDto.getNumeroCartao())) {
            throw new CartaoExistenteException("Já existe um Cartão cadastrado com esse número");
        }
        Cartao cartao = new Cartao(cartaoDto.getNumeroCartao(),
                passwordEncoder.encode(cartaoDto.getSenha()));
        repository.save(cartao);

        return toDto(cartao);
    }

    public BigDecimal consultaSaldoCartao(String numeroCartao) {
        return findCartaoByNumeroCartao(numeroCartao).getSaldo();
    }

    public Cartao findCartaoByNumeroCartao(String numeroCartao) {
        var cartao = repository.findByNumeroCartao(numeroCartao);
        if (isNull(cartao)) {
            throw new CartaoNaoEcnontradoException("Cartão não encontrado");
        }
        return cartao;
    }

    private boolean validaCartaoExistente(String numeroCartao) {
        return repository.existsByNumeroCartao(numeroCartao);
    }

    private CartaoDto toDto(Cartao entity) {
        CartaoDto cartaoDto = new CartaoDto();
        cartaoDto.setNumeroCartao(entity.getNumeroCartao());
        cartaoDto.setSaldo(entity.getSaldo());
        return cartaoDto;
    }

}
