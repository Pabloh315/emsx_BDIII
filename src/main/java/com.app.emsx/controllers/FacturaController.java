package com.app.emsx.controllers;

import com.app.emsx.common.ApiResponse;
import com.app.emsx.entities.Factura;
import com.app.emsx.repositories.FacturaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FacturaController
 * -----------------------------------------------------
 * ✔ CRUD completo para facturas
 * ✔ Formato de respuesta unificado ApiResponse
 */
@Slf4j
@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaRepository facturaRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Factura>>> findAll() {
        try {
            List<Factura> facturas = facturaRepository.findAll();
            log.info("✅ Listado de facturas: {} registros", facturas.size());
            return ResponseEntity.ok(ApiResponse.ok("Lista de facturas obtenida correctamente", facturas));
        } catch (Exception e) {
            log.error("❌ Error al listar facturas: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Error al obtener facturas: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Factura>> findById(@PathVariable Long id) {
        try {
            Factura factura = facturaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
            log.info("✅ Factura encontrada: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Factura encontrada", factura));
        } catch (Exception e) {
            log.error("❌ Error al buscar factura {}: {}", id, e.getMessage());
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("Factura no encontrada: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Factura>> create(@RequestBody Factura factura) {
        try {
            Factura saved = facturaRepository.save(factura);
            log.info("✅ Factura creada: {}", saved.getIdFactura());
            return ResponseEntity.ok(ApiResponse.ok("Factura creada correctamente", saved));
        } catch (Exception e) {
            log.error("❌ Error al crear factura: {}", e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al crear factura: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Factura>> update(@PathVariable Long id, @RequestBody Factura factura) {
        try {
            Factura existing = facturaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
            factura.setIdFactura(id);
            Factura updated = facturaRepository.save(factura);
            log.info("✅ Factura actualizada: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Factura actualizada correctamente", updated));
        } catch (Exception e) {
            log.error("❌ Error al actualizar factura {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al actualizar factura: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            facturaRepository.deleteById(id);
            log.info("✅ Factura eliminada: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Factura eliminada correctamente", null));
        } catch (Exception e) {
            log.error("❌ Error al eliminar factura {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al eliminar factura: " + e.getMessage()));
        }
    }
}



