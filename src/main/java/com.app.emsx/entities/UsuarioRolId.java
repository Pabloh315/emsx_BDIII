package com.app.emsx.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * UsuarioRolId
 * -----------------------------------------------------
 * ✔ Clave compuesta para UsuarioRol
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRolId implements Serializable {
    private Long usuarioId;
    private Long rolId;
}

