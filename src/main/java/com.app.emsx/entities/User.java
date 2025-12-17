package com.app.emsx.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User Entity
 * -----------------------------------------------------
 * ✔ Representa un usuario del sistema EMS
 * ✔ Implementa UserDetails para integración con Spring Security
 * ✔ Incluye rol (ROLE_ADMIN o ROLE_USER)
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(exclude = {"password"}) // ✅ Excluir password del constructor para forzar uso de setter
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true) // Temporalmente nullable para permitir migración de datos existentes
    private String firstname;

    @Column(nullable = true) // Temporalmente nullable para permitir migración de datos existentes
    private String lastname;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter(AccessLevel.NONE) // ✅ Deshabilitar setter automático de Lombok - usar setter personalizado
    @Column(nullable = false)
    private String password;

    /**
     * ✅ Setter protegido para password
     * Valida que el password esté cifrado (formato BCrypt: $2a$10$...)
     * Si se intenta setear un password en texto plano, lanza excepción
     * 
     * NOTA: Este setter NO debe usarse directamente.
     * Usar passwordEncoder.encode() en el servicio antes de llamar setPassword()
     */
    public void setPassword(String password) {
        // Permitir null solo durante construcción inicial
        if (password == null) {
            this.password = null;
            return;
        }
        
        // Validar que el password esté cifrado (formato BCrypt)
        // BCrypt siempre empieza con $2a$, $2b$ o $2y$ seguido de $ y luego el salt
        if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
            throw new IllegalArgumentException(
                "❌ SEGURIDAD: El password debe estar cifrado con BCrypt antes de asignarse. " +
                "Use passwordEncoder.encode() en el servicio. " +
                "Password recibido no tiene formato BCrypt válido."
            );
        }
        
        this.password = password;
    }

    /**
     * Relación many-to-many con roles a través de usuario_rol
     */
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UsuarioRol> usuarioRoles = null;

    /**
     * ✅ Devuelve la lista de roles del usuario desde usuario_rol
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (usuarioRoles != null && !usuarioRoles.isEmpty()) {
            return usuarioRoles.stream()
                    .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre()))
                    .collect(java.util.stream.Collectors.toList());
        }
        // Fallback si no hay roles asignados
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * ✅ El username para Spring Security será el campo username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * ✅ Indica si la cuenta está activa
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
