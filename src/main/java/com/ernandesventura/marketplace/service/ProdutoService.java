package com.ernandesventura.marketplace.service;

import com.ernandesventura.marketplace.dto.ProdutoRequest;
import com.ernandesventura.marketplace.dto.ProdutoResponse;
import com.ernandesventura.marketplace.exception.EstoqueInsuficienteException;
import com.ernandesventura.marketplace.exception.RecursoNaoEncontradoException;
import com.ernandesventura.marketplace.model.Produto;
import com.ernandesventura.marketplace.model.Usuario;
import com.ernandesventura.marketplace.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;

    public ProdutoService(ProdutoRepository produtoRepository, UsuarioService usuarioService) {
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
    }

    public ProdutoResponse criar(ProdutoRequest request) {
        Usuario vendedor = usuarioService.buscarEntidadePorId(request.vendedorId());
        Produto produto = new Produto(request.nome(), request.descricao(), request.preco(),
                request.quantidadeEstoque(), vendedor);
        Produto produtoSalvo = produtoRepository.save(produto);
        return ProdutoResponse.fromEntity(produtoSalvo);
    }

    public List<ProdutoResponse> listarTodos() {
        List<ProdutoResponse> resposta = new ArrayList<>();
        for (Produto produto : produtoRepository.findAll()) {
            resposta.add(ProdutoResponse.fromEntity(produto));
        }
        return resposta;
    }

    public ProdutoResponse buscarPorId(Long id) {
        Produto produto = buscarEntidadePorId(id);
        return ProdutoResponse.fromEntity(produto);
    }

    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidadePorId(id);
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setQuantidadeEstoque(request.quantidadeEstoque());
        Produto produtoAtualizado = produtoRepository.save(produto);
        return ProdutoResponse.fromEntity(produtoAtualizado);
    }

    public void deletar(Long id) {
        Produto produto = buscarEntidadePorId(id);
        produtoRepository.delete(produto);
    }

    public Produto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    public void debitarEstoque(Produto produto, Integer quantidade) {
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
        }
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);
    }
}
