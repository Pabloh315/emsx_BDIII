package com.app.emsx.services;

import com.app.emsx.dto.LoginRequest;
import com.app.emsx.dtos.auth.*;

/**
 * AuthService
 * -----------------------------------------------------
 * ✔ Define las operaciones públicas del servicio de autenticación
 */
public interface AuthService {

    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse login(LoginRequest request);

    AuthenticationResponse createAdminUser(); // ✅ Añadido aquí
}
