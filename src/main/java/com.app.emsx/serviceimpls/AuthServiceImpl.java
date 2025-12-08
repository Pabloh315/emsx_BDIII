package com.app.emsx.serviceimpls;

import com.app.emsx.dto.LoginRequest;
import com.app.emsx.dtos.auth.AuthenticationRequest;
import com.app.emsx.dtos.auth.AuthenticationResponse;
import com.app.emsx.dtos.auth.RegisterRequest;
import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import com.app.emsx.security.JwtService;
import com.app.emsx.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl
 * -----------------------------------------------------
 * ✔ Gestiona registro y autenticación de usuarios
 * ✔ Genera tokens JWT válidos con roles incluidos
 * ✔ Retorna la respuesta de autenticación al frontend
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * ✅ Registrar un nuevo usuario (modo desarrollo con defaults)
     */
    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();

        // Evitar errores de null en firstname / lastname
        user.setFirstname(
                request.getFirstname() != null && !request.getFirstname().isBlank()
                        ? request.getFirstname()
                        : "User"
        );
        user.setLastname(
                request.getLastname() != null && !request.getLastname().isBlank()
                        ? request.getLastname()
                        : "Default"
        );

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_ADMIN"); // Temporal para desarrollo

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();
    }

    /**
     * ✅ Autenticar usuario existente
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("❌ Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();
    }

    /**
     * ✅ Login de usuario existente usando LoginRequest
     */
    public AuthenticationResponse login(LoginRequest request) {
        // Tratamos el username como email para la autenticación
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("❌ Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();
    }

    /**
     * ✅ Crear usuario administrador por defecto
     */
    @Override
    public AuthenticationResponse createAdminUser() {
        // Verificar si ya existe un admin
        if (userRepository.findByEmail("admin@emsx.com").isPresent()) {
            User existingAdmin = userRepository.findByEmail("admin@emsx.com").get();
            String jwtToken = jwtService.generateToken(existingAdmin);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .userId(existingAdmin.getId())
                    .email(existingAdmin.getEmail())
                    .firstname(existingAdmin.getFirstname())
                    .lastname(existingAdmin.getLastname())
                    .role(existingAdmin.getRole())
                    .build();
        }

        // Crear nuevo admin
        User admin = User.builder()
                .firstname("Admin")
                .lastname("System")
                .email("admin@emsx.com")
                .password(passwordEncoder.encode("admin123"))
                .role("ROLE_ADMIN")
                .build();

        userRepository.save(admin);

        String jwtToken = jwtService.generateToken(admin);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(admin.getId())
                .email(admin.getEmail())
                .firstname(admin.getFirstname())
                .lastname(admin.getLastname())
                .role(admin.getRole())
                .build();
    }
}
