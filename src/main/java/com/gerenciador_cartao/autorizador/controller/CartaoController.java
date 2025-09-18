package com.gerenciador_cartao.autorizador.controller;

import com.gerenciador_cartao.autorizador.dto.CartaoDto;
import com.gerenciador_cartao.autorizador.service.CartaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    @PostMapping
    public ResponseEntity<CartaoDto> cadastrarCartao(@Valid @RequestBody CartaoDto cartaoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartaoService.cadastrarCartao(cartaoDto));
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> getSaldoCartao(@PathVariable String numeroCartao) {
        return ResponseEntity.status(HttpStatus.OK).body(cartaoService.consultaSaldoCartao(numeroCartao));
    }
}
