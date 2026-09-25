package com.ernandesventura.marketplace.dto;

import java.math.BigDecimal;

public record ProdutoRequest(String nome, String descricao, BigDecimal preco, Integer quantidadeEstoque, Long vendedorId) {
}
