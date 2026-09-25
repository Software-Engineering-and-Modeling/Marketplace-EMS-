package com.ernandesventura.marketplace.repository;
import com.ernandesventura.marketplace.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
