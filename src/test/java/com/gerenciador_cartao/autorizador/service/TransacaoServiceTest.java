package com.gerenciador_cartao.autorizador.service;

import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.dto.TransacaoDto;
import com.gerenciador_cartao.autorizador.exception.CartaoNaoEncontradoException;
import com.gerenciador_cartao.autorizador.exception.TransacaoException;
import com.gerenciador_cartao.autorizador.model.Status;
import com.gerenciador_cartao.autorizador.repository.CartaoRepository;
import com.gerenciador_cartao.autorizador.repository.TransacaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TransacaoServiceTest {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CartaoService cartaoService;

    @Autowired
    private TransacaoService transacaoService;

    @BeforeEach
    void setup() {
        cadastraCartoes();
    }

    @AfterEach
    void destroy() {
        cartaoRepository.deleteAll();
        transacaoRepository.deleteAll();
    }

    private void cadastraCartoes() {
        CartaoDto cartao1 = new CartaoDto("1231232533312366", "1234");
        cartaoService.cadastrarCartao(cartao1);
        CartaoDto cartao2 = new CartaoDto("6699312383423366", "5678");
        cartaoService.cadastrarCartao(cartao2);
    }

    @Test
    @DisplayName("Deve realizar transação em cartão existente")
    void transacaoSucessoTest() {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(50.25));
        dto.setSenha("5678");
        dto.setNumeroCartao("6699312383423366");

        var transacao = transacaoService.realizaTransacao(dto);

        assertNotNull(transacao.getDataTransacao());
        assertEquals(dto.getNumeroCartao(), transacao.getNumeroCartao());
        assertEquals(Status.SUCESSO, transacao.getStatus());
    }

    @Test
    @DisplayName("Não deve realizar transação com senha incorreta")
    void transacaoSenhaInvalidTest() {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(50.25));
        dto.setSenha("1234");
        dto.setNumeroCartao("6699312383423366");

        TransacaoException ex = assertThrows(TransacaoException.class,
                () -> transacaoService.realizaTransacao(dto));
        assertEquals(Status.SENHA_INVALIDA.toString(), ex.getMessage());
    }

    @Test
    @DisplayName("Não deve realizar transação com cartão inexistente")
    void transacaoCartaoInexistenteTest() {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(50.25));
        dto.setSenha("1234");
        dto.setNumeroCartao("6699396983423366");

        CartaoNaoEncontradoException ex = assertThrows(CartaoNaoEncontradoException.class,
                () -> transacaoService.realizaTransacao(dto));
        assertEquals("Cartão não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve realizar transação com valor maior que o saldo disponível")
    void transacaoValorMaiorSaldoTest() {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(650.25));
        dto.setSenha("1234");
        dto.setNumeroCartao("1231232533312366");

        TransacaoException ex = assertThrows(TransacaoException.class,
                () -> transacaoService.realizaTransacao(dto));
        assertEquals(Status.SALDO_INSUFICIENTE.toString(), ex.getMessage());
    }

}
