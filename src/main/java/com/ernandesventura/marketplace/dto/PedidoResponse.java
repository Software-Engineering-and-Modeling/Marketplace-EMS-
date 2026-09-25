package com.ernandesventura.marketplace.dto;

import com.ernandesventura.marketplace.model.ItemPedido;
import com.ernandesventura.marketplace.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record PedidoResponse(Long id, Long compradorId, String compradorNome,
                             LocalDateTime dataPedido, List<ItemPedidoResponse> itens, BigDecimal total) {
    public static PedidoResponse fromEntity(Pedido pedido) {
        List<ItemPedidoResponse> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : pedido.getItens()) {
            itens.add(ItemPedidoResponse.fromEntity(item));
            total = total.add(item.getSubtotal());
        }

        return new PedidoResponse(
                pedido.getId(),
                pedido.getComprador().getId(),
                pedido.getComprador().getNome(),
                pedido.getDataPedido(),
                itens,
                total
        );
    }
}