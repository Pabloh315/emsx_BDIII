package com.app.emsx.security;

import com.app.emsx.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ApplicationConfig
 * -----------------------------------------------------
 * ✔ Define beans principales de seguridad
 * ✔ Incluye UserDetailsService, PasswordEncoder y AuthenticationManager
 * ✔ Integra el UserRepository con Spring Security
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * ✅ Carga de usuario personalizada con roles desde usuario_rol
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findWithRolesByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("❌ Usuario no encontrado: " + username));
    }

    /**
     * ✅ Proveedor de autenticación (DAO)
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * ✅ Codificador de contraseñas (NoOp - SOLO DESARROLLO)
     * ⚠️ NO usar en producción - contraseñas en texto plano
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * ✅ Gestor de autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
