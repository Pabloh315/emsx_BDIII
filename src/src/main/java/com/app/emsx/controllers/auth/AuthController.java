package com.app.emsx.controllers;

import com.app.emsx.dtos.auth.AuthenticationRequest;
import com.app.emsx.dtos.auth.AuthenticationResponse;
import com.app.emsx.dtos.auth.RegisterRequest;
import com.app.emsx.servicesimpls.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * -----------------------------------------------------
 * ✔ Controlador REST para autenticación y registro
 * ✔ Expone endpoints: /api/auth/register y /api/auth/login
 * ✔ Devuelve token JWT y datos del usuario
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Permitir acceso desde el frontend (localhost:3000)
public class AuthController {

    private final AuthServiceImpl authService;

    /**
     * ✅ Registro de nuevo usuario
     * Endpoint: POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * ✅ Login de usuario existente
     * Endpoint: POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
