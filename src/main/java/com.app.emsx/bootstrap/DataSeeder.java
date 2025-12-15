package com.app.emsx.bootstrap;

import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * DataSeeder
 * -----------------------------------------------------
 * ✔ Crea usuario admin por defecto si no existe ninguno
 * ⚠️ SOLO DESARROLLO - contraseña en texto plano
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;

  @Override
  public void run(String... args) {
    if (userRepository.count() == 0) {
      // Crear usuario admin usando setters (no builder por problemas con usuarioRoles)
      User admin = new User();
      admin.setFirstname("Admin");
      admin.setLastname("Root");
      admin.setUsername("admin");
      admin.setEmail("admin@emsx.local");
      // ⚠️ NO hashear contraseña - SOLO DESARROLLO (NoOpPasswordEncoder)
      admin.setPassword("admin123");
      admin.setUsuarioRoles(null); // Los roles se asignarán desde usuario_rol si es necesario
      
      userRepository.save(admin);
      System.out.println("✔ Seeded default admin: username=admin, password=admin123");
    }
  }
}

