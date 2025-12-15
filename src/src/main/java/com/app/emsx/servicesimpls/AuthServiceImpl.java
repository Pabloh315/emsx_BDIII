package com.app.emsx.servicesimpls;

import com.app.emsx.dtos.auth.AuthenticationRequest;
import com.app.emsx.dtos.auth.AuthenticationResponse;
import com.app.emsx.dtos.auth.LoginResponseData;
import com.app.emsx.dtos.auth.RegisterRequest;
import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import com.app.emsx.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthServiceImpl
 * -----------------------------------------------------
 * ✔ Gestiona registro y autenticación de usuarios
 * ✔ Genera tokens JWT válidos con roles incluidos
 * ✔ Retorna la respuesta de autenticación al frontend
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * ✅ Registrar un nuevo usuario (modo desarrollo con defaults)
     */
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("📝 Registro de nuevo usuario: {}", request.getUsername());
        
        // Verificar si el username ya existe
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("❌ Username ya existe: {}", request.getUsername());
            throw new RuntimeException("El username ya está en uso");
        }
        
        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("❌ Email ya existe: {}", request.getEmail());
            throw new RuntimeException("El email ya está en uso");
        }
        
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

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER"); // Rol por defecto

        userRepository.save(user);
        log.info("✅ Usuario registrado exitosamente: {} (ID: {})", user.getUsername(), user.getId());

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
        log.info("🔐 Intentando autenticar usuario: {}", request.getUsername());
        
        // Buscar usuario por username o email
        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> {
                    log.error("❌ Usuario no encontrado: {}", request.getUsername());
                    return new RuntimeException("Usuario no encontrado");
                });

        log.info("✅ Usuario encontrado: {} (ID: {})", user.getUsername(), user.getId());

        try {
            // Autenticar con el username real del usuario (puede ser username o email)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()
                    )
            );
            log.info("✅ Autenticación exitosa para usuario: {}", user.getUsername());
        } catch (BadCredentialsException e) {
            log.error("❌ Credenciales incorrectas para usuario: {}", request.getUsername());
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        String jwtToken = jwtService.generateToken(user);
        log.info("✅ Token JWT generado para usuario: {}", user.getUsername());

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
     * ✅ Autenticar usuario existente y retornar formato esperado por frontend
     */
    public LoginResponseData authenticateForLogin(AuthenticationRequest request) {
        AuthenticationResponse authResponse = authenticate(request);
        
        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        LoginResponseData.UserInfo userInfo = LoginResponseData.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
        
        return LoginResponseData.builder()
                .user(userInfo)
                .token(authResponse.getToken())
                .build();
    }
}
