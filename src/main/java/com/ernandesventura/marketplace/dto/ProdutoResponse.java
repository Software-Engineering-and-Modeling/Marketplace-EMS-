package com.ernandesventura.marketplace.dto;

import com.ernandesventura.marketplace.model.Produto;

import java.math.BigDecimal;

public record ProdutoResponse(Long id, String nome, String descricao,
                              BigDecimal preco, Integer quantidadeEstoque, Long vendedorId, String vendedorNome) {
    public static ProdutoResponse fromEntity(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getVendedor().getId(),
                produto.getVendedor().getNome()
        );
    }

}
