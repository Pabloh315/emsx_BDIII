package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.auth.AuthenticationRequest;
import com.app.emsx.dtos.auth.AuthenticationResponse;
import com.app.emsx.dtos.auth.LoginResponseData;
import com.app.emsx.dtos.auth.RegisterRequest;
import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import com.app.emsx.security.JwtService;
import com.app.emsx.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl
 * -----------------------------------------------------
 * ✔ Gestiona registro y autenticación de usuarios
 * ✔ Genera tokens JWT válidos con roles incluidos
 * ✔ Retorna la respuesta de autenticación al frontend
 * ✔ Usa BCryptPasswordEncoder para cifrado seguro de contraseñas
 */
@Slf4j
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
        
        // ✅ Cifrar contraseña con BCrypt antes de guardar
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("🔐 Password cifrado para usuario {}: {}...", request.getUsername(), 
                  encodedPassword.substring(0, Math.min(20, encodedPassword.length())));
        user.setPassword(encodedPassword);

        userRepository.save(user);
        log.info("✅ Usuario registrado exitosamente: {} (ID: {})", user.getUsername(), user.getId());
        log.debug("🔐 Password guardado en BD (verificar formato BCrypt): {}...", 
                  user.getPassword().substring(0, Math.min(20, user.getPassword().length())));

        String jwtToken = jwtService.generateToken(user);
        
        // Obtener el primer rol del usuario (si existe)
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail() != null ? user.getEmail() : "")
                .firstname(user.getFirstname() != null ? user.getFirstname() : "Usuario")
                .lastname(user.getLastname() != null ? user.getLastname() : "Sin Apellido")
                .role(role)
                .build();
    }

    /**
     * ✅ Autenticar usuario existente
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("🔐 Intentando autenticar usuario: {}", request.getUsername());
        
        // Buscar usuario por username o email con roles cargados
        User user = userRepository.findWithRolesByUsernameOrEmail(request.getUsername(), request.getUsername())
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
        
        // Obtener el primer rol del usuario (si existe)
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail() != null ? user.getEmail() : "")
                .firstname(user.getFirstname() != null ? user.getFirstname() : "Usuario")
                .lastname(user.getLastname() != null ? user.getLastname() : "Sin Apellido")
                .role(role)
                .build();
    }
    
    /**
     * ✅ Autenticar usuario existente y retornar formato esperado por frontend
     */
    public LoginResponseData authenticateForLogin(AuthenticationRequest request) {
        try {
            AuthenticationResponse authResponse = authenticate(request);
            
            // Obtener usuario con roles cargados
            User userWithRoles = userRepository.findWithRolesByUsernameOrEmail(request.getUsername(), request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            LoginResponseData.UserInfo userInfo = LoginResponseData.UserInfo.builder()
                    .id(userWithRoles.getId())
                    .username(userWithRoles.getUsername() != null ? userWithRoles.getUsername() : "")
                    .email(userWithRoles.getEmail() != null ? userWithRoles.getEmail() : "")
                    .build();
            
            return LoginResponseData.builder()
                    .user(userInfo)
                    .token(authResponse.getToken())
                    .build();
        } catch (Exception e) {
            log.error("❌ Error en authenticateForLogin: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * ✅ Login de usuario existente usando LoginRequest (método legacy)
     */
    public AuthenticationResponse login(com.app.emsx.dto.LoginRequest request) {
        // Tratamos el username como email para la autenticación
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new RuntimeException("❌ Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);
        
        // Obtener el primer rol del usuario (si existe)
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail() != null ? user.getEmail() : "")
                .firstname(user.getFirstname() != null ? user.getFirstname() : "Usuario")
                .lastname(user.getLastname() != null ? user.getLastname() : "Sin Apellido")
                .role(role)
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
            
            String role = existingAdmin.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("ROLE_ADMIN");
            
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .userId(existingAdmin.getId())
                    .email(existingAdmin.getEmail() != null ? existingAdmin.getEmail() : "")
                    .firstname(existingAdmin.getFirstname() != null ? existingAdmin.getFirstname() : "Admin")
                    .lastname(existingAdmin.getLastname() != null ? existingAdmin.getLastname() : "System")
                    .role(role)
                    .build();
        }

        // Crear nuevo admin
        User admin = new User();
        admin.setFirstname("Admin");
        admin.setLastname("System");
        admin.setUsername("admin");
        admin.setEmail("admin@emsx.com");
        // ✅ Cifrar contraseña con BCrypt antes de guardar
        String encodedAdminPassword = passwordEncoder.encode("admin123");
        log.debug("🔐 Password cifrado para admin: {}...", 
                  encodedAdminPassword.substring(0, Math.min(20, encodedAdminPassword.length())));
        admin.setPassword(encodedAdminPassword);
        admin.setUsuarioRoles(null); // Se asignarán roles después si es necesario

        userRepository.save(admin);

        String jwtToken = jwtService.generateToken(admin);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(admin.getId())
                .email(admin.getEmail() != null ? admin.getEmail() : "")
                .firstname(admin.getFirstname() != null ? admin.getFirstname() : "Admin")
                .lastname(admin.getLastname() != null ? admin.getLastname() : "System")
                .role("ROLE_ADMIN")
                .build();
    }
}
