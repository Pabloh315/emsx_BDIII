package com.app.emsx.controllers.auth;

import com.app.emsx.common.ApiResponse;
import com.app.emsx.dtos.auth.AuthenticationRequest;
import com.app.emsx.dtos.auth.AuthenticationResponse;
import com.app.emsx.dtos.auth.LoginResponseData;
import com.app.emsx.dtos.auth.RegisterRequest;
import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import com.app.emsx.serviceimpls.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * -----------------------------------------------------
 * ✔ /register → registra un nuevo usuario
 * ✔ /login → devuelve token y datos del usuario
 * ✔ /me → devuelve el usuario autenticado (JWT requerido)
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final UserRepository userRepository;

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

    /**
     * ✅ Devuelve los datos del usuario autenticado según el token JWT
     * Endpoint: GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.fail("Usuario no autenticado"));
            }

            String username = authentication.getName();
            User user = userRepository.findByUsernameOrEmail(username, username)
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.fail("Usuario no encontrado"));
            }

            // Ocultamos la contraseña antes de devolver
            user.setPassword(null);
            return ResponseEntity.ok(ApiResponse.ok("Usuario autenticado", user));

        } catch (Exception e) {
            log.error("❌ Error al obtener usuario autenticado: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail("Error al obtener usuario autenticado: " + e.getMessage()));
        }
    }
}
