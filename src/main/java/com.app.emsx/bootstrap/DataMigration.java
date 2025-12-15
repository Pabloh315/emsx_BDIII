package com.app.emsx.bootstrap;

import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataMigration
 * -----------------------------------------------------
 * ✔ Actualiza datos existentes en la BD para migración
 * ✔ Se ejecuta al iniciar la aplicación
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataMigration {

    private final UserRepository userRepository;

    @PostConstruct
    @Transactional
    public void migrateExistingUsers() {
        try {
            log.info("🔄 Iniciando migración de datos existentes...");
            
            // Actualizar usuarios que tengan firstname, lastname o username NULL
            userRepository.findAll().forEach(user -> {
                boolean updated = false;
                
                if (user.getFirstname() == null || user.getFirstname().isBlank()) {
                    // Intentar extraer nombre del email si es posible
                    if (user.getEmail() != null && user.getEmail().contains("@")) {
                        String emailPart = user.getEmail().split("@")[0];
                        user.setFirstname(emailPart.substring(0, Math.min(emailPart.length(), 50)));
                    } else {
                        user.setFirstname("Usuario");
                    }
                    updated = true;
                }
                
                if (user.getLastname() == null || user.getLastname().isBlank()) {
                    user.setLastname("Sin Apellido");
                    updated = true;
                }
                
                if (user.getUsername() == null || user.getUsername().isBlank()) {
                    // Si no tiene username, usar email como username o generar uno
                    if (user.getEmail() != null && !user.getEmail().isBlank()) {
                        user.setUsername(user.getEmail().split("@")[0]);
                    } else {
                        user.setUsername("user" + user.getId());
                    }
                    updated = true;
                }
                
                if (updated) {
                    userRepository.save(user);
                    log.info("✅ Usuario migrado: ID={}, username={}, firstname={}, lastname={}", 
                            user.getId(), user.getUsername(), user.getFirstname(), user.getLastname());
                }
            });
            
            log.info("✅ Migración de datos completada");
        } catch (Exception e) {
            log.error("❌ Error en migración de datos: {}", e.getMessage(), e);
            // No lanzar excepción para que la aplicación pueda iniciar
        }
    }
}

