package com.app.emsx.controllers;

import com.app.emsx.common.ApiResponse;
import com.app.emsx.dtos.auth.AuthenticationRequest;
import com.app.emsx.dtos.auth.AuthenticationResponse;
import com.app.emsx.dtos.auth.LoginResponseData;
import com.app.emsx.dtos.auth.RegisterRequest;
import com.app.emsx.servicesimpls.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * -----------------------------------------------------
 * ✔ Controlador REST para autenticación y registro
 * ✔ Expone endpoints: /api/auth/register y /api/auth/login
 * ✔ Devuelve token JWT y datos del usuario en formato unificado
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Permitir acceso desde app móvil
public class AuthController {

    private final AuthServiceImpl authService;

    /**
     * ✅ Registro de nuevo usuario
     * Endpoint: POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @RequestBody RegisterRequest request
    ) {
        try {
            log.info("📝 Registro de nuevo usuario: {}", request.getEmail());
            AuthenticationResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.ok("Usuario registrado exitosamente", response));
        } catch (Exception e) {
            log.error("❌ Error en registro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error al registrar usuario: " + e.getMessage()));
        }
    }

    /**
     * ✅ Login de usuario existente
     * Endpoint: POST /api/auth/login
     * Formato de respuesta esperado por frontend:
     * {
     *   "success": true,
     *   "message": "Login exitoso",
     *   "data": {
     *     "user": { "id": 1, "username": "admin", "email": "..." },
     *     "token": "JWT_TOKEN"
     *   },
     *   "timestamp": "ISO_DATE"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseData>> login(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            log.info("🔐 Intento de login para usuario: {}", request.getUsername());
            LoginResponseData loginData = authService.authenticateForLogin(request);
            log.info("✅ Login exitoso para usuario: {}", request.getUsername());
            return ResponseEntity.ok(ApiResponse.ok("Login exitoso", loginData));
        } catch (BadCredentialsException e) {
            log.error("❌ Credenciales incorrectas para usuario: {}", request.getUsername());
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail("Credenciales incorrectas"));
        } catch (Exception e) {
            log.error("❌ Error en login: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Error al autenticar: " + e.getMessage()));
        }
    }
}
