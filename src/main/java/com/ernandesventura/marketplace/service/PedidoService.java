package com.ernandesventura.marketplace.service;

import com.ernandesventura.marketplace.dto.ItemPedidoRequest;
import com.ernandesventura.marketplace.dto.PedidoRequest;
import com.ernandesventura.marketplace.dto.PedidoResponse;
import com.ernandesventura.marketplace.exception.OperacaoInvalidaException;
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
    // compradorId = id do usuário logado (vem do token, não do JSON)
    public PedidoResponse criar(PedidoRequest request, Long compradorId) {
        Usuario comprador = usuarioService.buscarEntidadePorId(compradorId);

        Pedido pedido = new Pedido(comprador, LocalDateTime.now());

        for (ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = produtoService.buscarEntidadePorId(itemRequest.produtoId());

            // Vendedor não compra o próprio produto. Como o método é @Transactional,
            // o estoque já debitado de itens anteriores é desfeito.
            if (produto.getVendedor().getId().equals(compradorId)) {
                throw new OperacaoInvalidaException("Você não pode comprar o próprio produto: " + produto.getNome());
            }

            Integer quantidade = itemRequest.quantidade();
            produtoService.debitarEstoque(produto, quantidade);

            ItemPedido item = new ItemPedido(pedido, produto, quantidade, produto.getPreco());
            pedido.getItens().add(item);
        }

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return PedidoResponse.fromEntity(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    // Só os pedidos em que o usuário logado é o comprador
    public List<PedidoResponse> listarPorComprador(Long compradorId) {
        List<Pedido> pedidos = pedidoRepository.findByCompradorId(compradorId);
        List<PedidoResponse> resposta = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            resposta.add(PedidoResponse.fromEntity(pedido));
        }

        return resposta;
    }
}
