package com.app.emsx.bootstrap;

import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * DataSeeder
 * -----------------------------------------------------
 * ✔ Crea usuario admin por defecto si no existe ninguno
 * ✔ Usa BCryptPasswordEncoder para cifrado seguro de contraseñas
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepository.count() == 0) {
      // Crear usuario admin usando setters (no builder por problemas con usuarioRoles)
      User admin = new User();
      admin.setFirstname("Admin");
      admin.setLastname("Root");
      admin.setUsername("admin");
      admin.setEmail("admin@emsx.local");
      // ✅ Cifrar contraseña con BCrypt antes de guardar
      String encodedPassword = passwordEncoder.encode("admin123");
      System.out.println("🔐 Password cifrado para admin: " + encodedPassword.substring(0, Math.min(20, encodedPassword.length())) + "...");
      admin.setPassword(encodedPassword);
      admin.setUsuarioRoles(null); // Los roles se asignarán desde usuario_rol si es necesario
      
      userRepository.save(admin);
      System.out.println("✔ Seeded default admin: username=admin, password=admin123");
    }
  }
}

