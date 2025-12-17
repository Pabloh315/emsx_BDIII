package com.app.emsx.repositories;

import com.app.emsx.entities.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @EntityGraph(attributePaths = {"cliente", "pedidoProductos", "pedidoProductos.producto"})
    List<Pedido> findAll();
    
    @EntityGraph(attributePaths = {"cliente", "pedidoProductos", "pedidoProductos.producto"})
    Optional<Pedido> findById(Long id);
}




