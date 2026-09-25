package com.ernandesventura.marketplace.repository;

import com.ernandesventura.marketplace.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByAtivoTrue();

    Optional<Usuario> findByIdAndAtivoTrue(Long id);
}
