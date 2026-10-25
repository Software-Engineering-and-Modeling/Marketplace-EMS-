package com.ernandesventura.marketplace.controller;

import com.ernandesventura.marketplace.dto.ProdutoRequest;
import com.ernandesventura.marketplace.dto.ProdutoResponse;
import com.ernandesventura.marketplace.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        List<ProdutoResponse> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        ProdutoResponse produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@RequestBody ProdutoRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        // O "subject" do token é o id do usuário logado (definido no AuthService)
        Long usuarioLogadoId = Long.valueOf(jwt.getSubject());
        ProdutoResponse produtoCriado = produtoService.criar(request, usuarioLogadoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @RequestBody ProdutoRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        Long usuarioLogadoId = Long.valueOf(jwt.getSubject());
        ProdutoResponse produtoAtualizado = produtoService.atualizar(id, request, usuarioLogadoId);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioLogadoId = Long.valueOf(jwt.getSubject());
        produtoService.inativar(id, usuarioLogadoId);
        return ResponseEntity.noContent().build();
    }
}
