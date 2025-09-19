package com.gerenciador_cartao.autorizador.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.dto.TransacaoDto;
import com.gerenciador_cartao.autorizador.repository.CartaoRepository;
import com.gerenciador_cartao.autorizador.service.CartaoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class TransacaoControllerTest {

    private static final String TRANSACAO_URL = "/transacoes";

    @Value("${api.login}")
    private String login;
    @Value("${api.senha}")
    private String senha;

    private String BASIC_AUTH = "";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private CartaoService cartaoService;

    @BeforeEach
    void setup() {
        BASIC_AUTH = gerarBasicAuth();
        cadastraCartoes();
    }

    @AfterEach
    void destroy() {
        cartaoRepository.deleteAll();
    }

    private void cadastraCartoes() {
        CartaoDto cartao1 = new CartaoDto("1231232533312366", "1234");
        cartaoService.cadastrarCartao(cartao1);
        CartaoDto cartao2 = new CartaoDto("6699312383423366", "5678");
        cartaoService.cadastrarCartao(cartao2);
    }

    String gerarBasicAuth() {

        String auth = login + ":" + senha;
        String encodedAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedAuth;
    }

    @Test
    @DisplayName("Deve realizar uma transação com sucesso")
    void realizaTransacaoSucessoTest() throws Exception {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(50.25));
        dto.setSenha("1234");
        dto.setNumeroCartao("1231232533312366");
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("1231232533312366"))
                .andExpect(jsonPath("$.valor").value(50));
    }

    @Test
    @DisplayName("Não deve realizar uma transação com senha incorreta")
    void transacaoSenhaInvalidaTest() throws Exception {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(50.25));
        dto.setSenha("4312");
        dto.setNumeroCartao("6699312383423366");
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Falha na transação"))
                .andExpect(jsonPath("$.message").value("SENHA_INVALIDA"));
    }

    @Test
    @DisplayName("Não deve realizar uma transação quando cartão inexistente")
    void transacaoCartaoInvalidoTest() throws Exception {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(50.25));
        dto.setSenha("4312");
        dto.setNumeroCartao("1999312383423366");
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cartão não encontrado"))
                .andExpect(jsonPath("$.message").value("Cartão não encontrado"));
    }

    @Test
    @DisplayName("Não deve realizar uma transação quando saldo insuficiente")
    void transacaoSaldoInsuficienteTest() throws Exception {
        TransacaoDto dto = new TransacaoDto();
        dto.setValor(new BigDecimal(750.12));
        dto.setSenha("5678");
        dto.setNumeroCartao("6699312383423366");
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Falha na transação"))
                .andExpect(jsonPath("$.message").value("SALDO_INSUFICIENTE"));
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void headerSemAutenticacaoTest() throws Exception {
        CartaoDto dto = new CartaoDto("1234567890123456", "1234");
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACAO_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
