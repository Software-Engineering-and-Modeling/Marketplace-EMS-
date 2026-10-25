package com.ernandesventura.marketplace.repository;
import com.ernandesventura.marketplace.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Navega Pedido.comprador.id — pedidos de um comprador
    List<Pedido> findByCompradorId(Long compradorId);
}
