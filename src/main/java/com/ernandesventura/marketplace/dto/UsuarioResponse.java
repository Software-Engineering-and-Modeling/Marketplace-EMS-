package com.ernandesventura.marketplace.dto;
import com.ernandesventura.marketplace.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}

