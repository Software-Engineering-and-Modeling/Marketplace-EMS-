package com.ernandesventura.marketplace.repository;
import com.ernandesventura.marketplace.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
