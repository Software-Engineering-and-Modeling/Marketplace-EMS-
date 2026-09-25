package com.ernandesventura.marketplace.dto;
import com.ernandesventura.marketplace.model.ItemPedido;
import java.math.BigDecimal;

public record ItemPedidoResponse(Long produtoId, String produtoNome, Integer quantidade,
                                 BigDecimal precoUnitario, BigDecimal subtotal) {

    public static ItemPedidoResponse fromEntity(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal()
        );
    }
}
