package com.gerenciador_cartao.autorizador.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.repository.CartaoRepository;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class CartaoControllerTest {

    private static final String CARTAO_URL = "/cartoes";

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

    @BeforeEach
    void setup() {
        BASIC_AUTH = gerarBasicAuth();
    }

    @AfterEach
    void destroy() {
        cartaoRepository.deleteAll();
    }

    String gerarBasicAuth() {

        String auth = login + ":" + senha;
        String encodedAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedAuth;
    }

    @Test
    @DisplayName("Deve criar cartão com sucesso")
    void criarCartaoSucessoTest() throws Exception {
        CartaoDto dto = new CartaoDto("9234567332227577", "1234");
        mockMvc.perform(MockMvcRequestBuilders.post(CARTAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value("9234567332227577"))
                .andExpect(jsonPath("$.saldo").value(500));
    }

    @Test
    @DisplayName("Não deve criar cartão com número existente")
    void cadastrarCartaoExistenteTest() throws Exception {
        CartaoDto dto = new CartaoDto("9234567832227577", "1234");
        mockMvc.perform(MockMvcRequestBuilders.post(CARTAO_URL)
                .header("Authorization", BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        mockMvc.perform(MockMvcRequestBuilders.post(CARTAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Já existe um Cartão cadastrado com esse número"))
                .andExpect(jsonPath("$.error").value("Cartão ja existente"));
    }

    @Test
    @DisplayName("Não deve criar cartão sem número")
    void cadastrarCartaoNumeroVazioTest() throws Exception {
        CartaoDto dto = new CartaoDto("", "1234");
        mockMvc.perform(MockMvcRequestBuilders.post(CARTAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.message", containsString("numeroCartao")));
    }

    @Test
    @DisplayName("Não deve criar cartão sem senha")
    void cadastrarCartaoSenhaVaziaTest() throws Exception {
        CartaoDto dto = new CartaoDto("9234567832227596", "");
        mockMvc.perform(MockMvcRequestBuilders.post(CARTAO_URL)
                        .header("Authorization", BASIC_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.message", containsString("senha")));
    }

    @Test
    @DisplayName("Deve retornar saldo cartão")
    void consultaSaldoCartaoTest() throws Exception {
        CartaoDto dto = new CartaoDto("9234567832227577", "1234");
        mockMvc.perform(MockMvcRequestBuilders.post(CARTAO_URL)
                .header("Authorization", BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(MockMvcRequestBuilders.get(CARTAO_URL + "/9234567832227577")
                        .header("Authorization", BASIC_AUTH))
                .andExpect(status().isOk())
                .andExpect(content().string("500"));
    }

    @Test
    @DisplayName("Deve retornar 404 cartão não encontrado")
    void consultaSaldoCartaoInexistenteTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(CARTAO_URL + "/9234567832258577")
                        .header("Authorization", BASIC_AUTH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cartão não encontrado"))
                .andExpect(jsonPath("$.message").value("Cartão não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 sem autenticação")
    void headerSemAutenticacaoTest() throws Exception {
        CartaoDto dto = new CartaoDto("1234567890123456", "1234");
        mockMvc.perform(MockMvcRequestBuilders.post(CARTAO_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
