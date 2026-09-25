package com.ernandesventura.marketplace.service;

import com.ernandesventura.marketplace.dto.ItemPedidoRequest;
import com.ernandesventura.marketplace.dto.PedidoRequest;
import com.ernandesventura.marketplace.dto.PedidoResponse;
import com.ernandesventura.marketplace.exception.EstoqueInsuficienteException;
import com.ernandesventura.marketplace.exception.RecursoNaoEncontradoException;
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
        Usuario comprador = usuarioService.buscarPorId(compradorId).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + compradorId));

        Pedido pedido = new Pedido(comprador, LocalDateTime.now());

        for (ItemPedidoRequest itemRequest : request.itens()) {
            Long produtoId = itemRequest.produtoId();
            Produto produto = produtoService.buscarPorId(produtoId).orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + produtoId));

            Integer quantidade = itemRequest.quantidade();
            if (produto.getQuantidadeEstoque() < quantidade) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
            produtoService.salvar(produto);

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
