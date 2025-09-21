package com.gerenciador_cartao.autorizador.service;

import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.exception.CartaoExistenteException;
import com.gerenciador_cartao.autorizador.exception.CartaoNaoEncontradoException;
import com.gerenciador_cartao.autorizador.model.Cartao;
import com.gerenciador_cartao.autorizador.repository.CartaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CartaoServiceTest {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private CartaoService cartaoService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        cadastraCartoes();
    }

    @AfterEach
    void destroy() {
        cartaoRepository.deleteAll();
    }

    private void cadastraCartoes() {
        CartaoDto cartao1 = new CartaoDto("1233333333333366", "1234");
        cartaoService.cadastrarCartao(cartao1);
        CartaoDto cartao2 = new CartaoDto("5233333444333366", "5678");
        cartaoService.cadastrarCartao(cartao2);
    }

    @Test
    @DisplayName("Cadastrar cartão e validar saldo inicial")
    void cadastrarCartaoTest() {

        CartaoDto dto = new CartaoDto();
        dto.setSenha("1234");
        dto.setNumeroCartao("3154618817122681");
        Cartao cartaoCriado = cartaoService.cadastrarCartao(dto);

        assertNotNull(cartaoCriado.getId());
        assertEquals("3154618817122681", cartaoCriado.getNumeroCartao());
        assertTrue(passwordEncoder.matches("1234", cartaoCriado.getSenha()));
        assertEquals(new BigDecimal("500"), cartaoCriado.getSaldo());
    }

    @Test
    @DisplayName("Não deve cadastrar cartão já existente")
    void cadastrarCartaoExistenteTest() {

        CartaoDto dto = new CartaoDto();
        dto.setSenha("1234");
        dto.setNumeroCartao("1233333333333366");

        CartaoExistenteException ex = assertThrows(CartaoExistenteException.class,
                () -> cartaoService.cadastrarCartao(dto));
        assertEquals("Já existe um Cartão cadastrado com esse número", ex.getMessage());
    }

    @Test
    @DisplayName("Consultar saldo de cartão cadastrado")
    void consultarSaldoCartaoTest() {

        BigDecimal saldo = cartaoService.consultaSaldoCartao("5233333444333366");

        assertNotNull(saldo);
        assertEquals(new BigDecimal("500"), saldo);
    }

    @Test
    @DisplayName("Consultar saldo de cartão inexistente")
    void consultarSaldoCartaoInexistenteTest() {

        CartaoNaoEncontradoException ex = assertThrows(CartaoNaoEncontradoException.class,
                () -> cartaoService.consultaSaldoCartao("6983333444333366"));
        assertEquals("Cartão não encontrado", ex.getMessage());
    }

}
