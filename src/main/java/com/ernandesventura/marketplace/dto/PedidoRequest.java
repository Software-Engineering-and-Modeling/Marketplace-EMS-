package com.ernandesventura.marketplace.dto;
import java.util.List;

// O comprador não vem no JSON: é o usuário logado (token)
public record PedidoRequest(List<ItemPedidoRequest> itens) {
}
