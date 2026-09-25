package com.ernandesventura.marketplace.service;

import com.ernandesventura.marketplace.dto.ItemPedidoRequest;
import com.ernandesventura.marketplace.dto.PedidoRequest;
import com.ernandesventura.marketplace.dto.PedidoResponse;
import com.ernandesventura.marketplace.model.ItemPedido;
import com.ernandesventura.marketplace.model.Pedido;
import com.ernandesventura.marketplace.model.Produto;
import com.ernandesventura.marketplace.model.Usuario;
import com.ernandesventura.marketplace.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioService usuarioService, ProdutoService produtoService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioService = usuarioService;
        this.produtoService = produtoService;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        Long compradorId = request.compradorId();
        Usuario comprador = usuarioService.buscarEntidadePorId(compradorId);

        Pedido pedido = new Pedido(comprador, LocalDateTime.now());

        for (ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = produtoService.buscarEntidadePorId(itemRequest.produtoId());

            Integer quantidade = itemRequest.quantidade();
            produtoService.debitarEstoque(produto, quantidade);

            ItemPedido item = new ItemPedido(pedido, produto, quantidade, produto.getPreco());
            pedido.getItens().add(item);
        }

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return PedidoResponse.fromEntity(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        List<PedidoResponse> resposta = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            resposta.add(PedidoResponse.fromEntity(pedido));
        }

        return resposta;
    }
}
