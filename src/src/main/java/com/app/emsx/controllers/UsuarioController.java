package com.app.emsx.controllers;

import com.app.emsx.common.ApiResponse;
import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UsuarioController
 * -----------------------------------------------------
 * ✔ CRUD completo para usuarios
 * ✔ Formato de respuesta unificado ApiResponse
 */
@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> findAll() {
        try {
            List<User> usuarios = userRepository.findAll();
            // Ocultar contraseñas
            usuarios.forEach(u -> u.setPassword(null));
            log.info("✅ Listado de usuarios: {} registros", usuarios.size());
            return ResponseEntity.ok(ApiResponse.ok("Lista de usuarios obtenida correctamente", usuarios));
        } catch (Exception e) {
            log.error("❌ Error al listar usuarios: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Error al obtener usuarios: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findById(@PathVariable Long id) {
        try {
            User usuario = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuario.setPassword(null);
            log.info("✅ Usuario encontrado: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", usuario));
        } catch (Exception e) {
            log.error("❌ Error al buscar usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("Usuario no encontrado: " + e.getMessage()));
        }
    }
}



