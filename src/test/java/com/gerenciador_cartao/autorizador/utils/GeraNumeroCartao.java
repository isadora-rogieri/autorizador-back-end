package com.gerenciador_cartao.autorizador.utils;

import java.util.Random;

public final class GeraNumeroCartao {

    public static String gerarNumero16Digitos() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
