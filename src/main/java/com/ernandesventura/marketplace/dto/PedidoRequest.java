package com.ernandesventura.marketplace.dto;
import java.util.List;

public record PedidoRequest(Long compradorId, List<ItemPedidoRequest> itens) {
}
