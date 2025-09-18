package com.gerenciador_cartao.autorizador.controller;

import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.dto.TransacaoDto;
import com.gerenciador_cartao.autorizador.service.CartaoService;
import com.gerenciador_cartao.autorizador.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<TransacaoDto> realizaTransacao(@Valid @RequestBody TransacaoDto transacaoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoService.realizaTransacao(transacaoDto));
    }
}
