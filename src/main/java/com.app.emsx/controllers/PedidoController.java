package com.app.emsx.controllers;

import com.app.emsx.common.ApiResponse;
import com.app.emsx.entities.Pedido;
import com.app.emsx.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PedidoController
 * -----------------------------------------------------
 * ✔ CRUD completo para pedidos
 * ✔ Formato de respuesta unificado ApiResponse
 */
@Slf4j
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Pedido>>> findAll() {
        try {
            List<Pedido> pedidos = pedidoRepository.findAll();
            log.info("✅ Listado de pedidos: {} registros", pedidos.size());
            return ResponseEntity.ok(ApiResponse.ok("Lista de pedidos obtenida correctamente", pedidos));
        } catch (Exception e) {
            log.error("❌ Error al listar pedidos: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pedido>> findById(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            log.info("✅ Pedido encontrado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Pedido encontrado", pedido));
        } catch (Exception e) {
            log.error("❌ Error al buscar pedido {}: {}", id, e.getMessage());
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("Pedido no encontrado: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Pedido>> create(@RequestBody Pedido pedido) {
        try {
            Pedido saved = pedidoRepository.save(pedido);
            log.info("✅ Pedido creado: {}", saved.getIdPedido());
            return ResponseEntity.ok(ApiResponse.ok("Pedido creado correctamente", saved));
        } catch (Exception e) {
            log.error("❌ Error al crear pedido: {}", e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al crear pedido: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Pedido>> update(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            Pedido existing = pedidoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            pedido.setIdPedido(id);
            Pedido updated = pedidoRepository.save(pedido);
            log.info("✅ Pedido actualizado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Pedido actualizado correctamente", updated));
        } catch (Exception e) {
            log.error("❌ Error al actualizar pedido {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al actualizar pedido: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            pedidoRepository.deleteById(id);
            log.info("✅ Pedido eliminado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Pedido eliminado correctamente", null));
        } catch (Exception e) {
            log.error("❌ Error al eliminar pedido {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al eliminar pedido: " + e.getMessage()));
        }
    }
}



