package com.ernandesventura.marketplace.repository;

import com.ernandesventura.marketplace.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrueAndVendedorAtivoTrue();

    Optional<Produto> findByIdAndAtivoTrueAndVendedorAtivoTrue(Long id);
}
