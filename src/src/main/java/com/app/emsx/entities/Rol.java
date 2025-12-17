package com.app.emsx.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Rol Entity
 * -----------------------------------------------------
 * ✔ Representa un rol del sistema (ADMIN, VENDEDOR, etc.)
 * ✔ Mapea a la tabla "roles" en la BD
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre; // ADMIN, VENDEDOR, etc.
}



