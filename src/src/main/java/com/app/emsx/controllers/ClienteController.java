package com.app.emsx.controllers;

import com.app.emsx.common.ApiResponse;
import com.app.emsx.entities.Cliente;
import com.app.emsx.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * ClienteController
 * -----------------------------------------------------
 * ✔ CRUD completo para clientes
 * ✔ Formato de respuesta unificado ApiResponse
 */
@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> findAll() {
        try {
            List<Cliente> clientes = clienteRepository.findAll();
            log.info("✅ Listado de clientes: {} registros", clientes.size());
            return ResponseEntity.ok(ApiResponse.ok("Lista de clientes obtenida correctamente", clientes));
        } catch (Exception e) {
            log.error("❌ Error al listar clientes: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Error al obtener clientes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> findById(@PathVariable Long id) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            log.info("✅ Cliente encontrado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Cliente encontrado", cliente));
        } catch (Exception e) {
            log.error("❌ Error al buscar cliente {}: {}", id, e.getMessage());
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("Cliente no encontrado: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> create(@RequestBody Cliente cliente) {
        try {
            Cliente saved = clienteRepository.save(cliente);
            log.info("✅ Cliente creado: {}", saved.getIdCliente());
            return ResponseEntity.ok(ApiResponse.ok("Cliente creado correctamente", saved));
        } catch (Exception e) {
            log.error("❌ Error al crear cliente: {}", e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al crear cliente: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> update(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            Cliente existing = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            cliente.setIdCliente(id);
            Cliente updated = clienteRepository.save(cliente);
            log.info("✅ Cliente actualizado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Cliente actualizado correctamente", updated));
        } catch (Exception e) {
            log.error("❌ Error al actualizar cliente {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al actualizar cliente: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            clienteRepository.deleteById(id);
            log.info("✅ Cliente eliminado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado correctamente", null));
        } catch (Exception e) {
            log.error("❌ Error al eliminar cliente {}: {}", id, e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.fail("Error al eliminar cliente: " + e.getMessage()));
        }
    }
}




