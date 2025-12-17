package com.app.emsx.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * UsuarioRol Entity
 * -----------------------------------------------------
 * ✔ Tabla de relación many-to-many entre usuarios y roles
 * ✔ Mapea a la tabla "usuario_rol" en la BD
 */
@Entity
@Table(name = "usuario_rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UsuarioRolId.class)
public class UsuarioRol {
    
    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @Id
    @Column(name = "rol_id")
    private Long rolId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private User usuario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", insertable = false, updatable = false)
    private Rol rol;
}



