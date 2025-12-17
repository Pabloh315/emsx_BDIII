package com.app.emsx.repositories;

import com.app.emsx.entities.UsuarioRol;
import com.app.emsx.entities.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {
    @Query("SELECT ur FROM UsuarioRol ur WHERE ur.usuarioId = :usuarioId")
    List<UsuarioRol> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}




