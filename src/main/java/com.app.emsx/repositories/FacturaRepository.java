package com.app.emsx.repositories;

import com.app.emsx.entities.Factura;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    @EntityGraph(attributePaths = {"pedido", "pedido.cliente", "pedido.pedidoProductos", "pedido.pedidoProductos.producto"})
    List<Factura> findAll();
    
    @EntityGraph(attributePaths = {"pedido", "pedido.cliente", "pedido.pedidoProductos", "pedido.pedidoProductos.producto"})
    Optional<Factura> findById(Long id);
}

