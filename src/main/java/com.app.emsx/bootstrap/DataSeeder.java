package com.app.emsx.bootstrap;

import com.app.emsx.entities.User;
import com.app.emsx.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (userRepository.count() == 0) {
      User admin = User.builder()
          .firstname("Admin")
          .lastname("Root")
          .email("admin@emsx.local")
          .password(passwordEncoder.encode("admin123"))
          .role("ROLE_ADMIN")
          .build();
      userRepository.save(admin);
      System.out.println("✔ Seeded default admin: admin@emsx.local / admin123");
    }
  }
}

