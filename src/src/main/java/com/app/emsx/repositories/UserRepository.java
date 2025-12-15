package com.app.emsx.repositories;

import com.app.emsx.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 * -----------------------------------------------------
 * ✔ Repositorio JPA para entidad User
 * ✔ Incluye método para buscar por email y username
 * ✔ Carga roles desde usuario_rol con EntityGraph
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email (para login)
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su username (para login)
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por username o email
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * Busca un usuario por username o email cargando roles (EAGER)
     */
    @EntityGraph(attributePaths = {"usuarioRoles", "usuarioRoles.rol"})
    Optional<User> findWithRolesByUsernameOrEmail(String username, String email);
}
