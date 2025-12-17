package com.app.emsx.controllers;

import com.app.emsx.common.ApiResponse;
import com.app.emsx.entities.Producto;
import com.app.emsx.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductoController
 * -----------------------------------------------------
 * ✔ CRUD completo para productos
 * ✔ Formato de respuesta unificado ApiResponse
 */
@Slf4j
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Producto>>> findAll() {
        try {
            List<Producto> productos = productoRepository.findAll();
            log.info("✅ Listado de productos: {} registros", productos.size());
            return ResponseEntity.ok(ApiResponse.ok("Lista de productos obtenida correctamente", productos));
        } catch (Exception e) {
            log.error("❌ Error al listar productos: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Error al obtener productos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> findById(@PathVariable Long id) {
        try {
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            log.info("✅ Producto encontrado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Producto encontrado", producto));
        } catch (Exception e) {
            log.error("❌ Error al buscar producto {}: {}", id, e.getMessage());
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("Producto no encontrado: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Producto>> create(@RequestBody Producto producto) {
        try {
            Producto saved = productoRepository.save(producto);
            log.info("✅ Producto creado: {}", saved.getIdProd());
            return ResponseEntity.ok(ApiResponse.ok("Producto creado correctamente", saved));
        } catch (Exception e) {
            log.error("❌ Error al crear producto: {}", e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al crear producto: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> update(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto existing = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            producto.setIdProd(id);
            Producto updated = productoRepository.save(producto);
            log.info("✅ Producto actualizado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Producto actualizado correctamente", updated));
        } catch (Exception e) {
            log.error("❌ Error al actualizar producto {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al actualizar producto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            productoRepository.deleteById(id);
            log.info("✅ Producto eliminado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Producto eliminado correctamente", null));
        } catch (Exception e) {
            log.error("❌ Error al eliminar producto {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al eliminar producto: " + e.getMessage()));
        }
    }
}




