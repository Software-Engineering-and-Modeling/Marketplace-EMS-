package com.ernandesventura.marketplace.controller;

import com.ernandesventura.marketplace.dto.UsuarioCadastroRequest;
import com.ernandesventura.marketplace.dto.UsuarioRequest;
import com.ernandesventura.marketplace.dto.UsuarioResponse;
import com.ernandesventura.marketplace.service.UsuarioService;
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

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioLogadoId = Long.valueOf(jwt.getSubject());
        UsuarioResponse usuario = usuarioService.buscarPorId(id, usuarioLogadoId);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@RequestBody UsuarioCadastroRequest request) {
        UsuarioResponse usuarioCriado = usuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, @RequestBody UsuarioRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        Long usuarioLogadoId = Long.valueOf(jwt.getSubject());
        UsuarioResponse usuarioAtualizado = usuarioService.atualizar(id, request, usuarioLogadoId);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioLogadoId = Long.valueOf(jwt.getSubject());
        usuarioService.inativar(id, usuarioLogadoId);
        return ResponseEntity.noContent().build();
    }
}
