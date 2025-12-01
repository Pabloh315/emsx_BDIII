package com.app.emsx.repositories;

import com.app.emsx.entities.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependentRepository extends JpaRepository<Dependent, Long> {

    /**
     * 🔹 Devuelve todos los dependientes asociados a un empleado específico.
     * Usa el ID del empleado como clave foránea.
     */
    List<Dependent> findByEmployeeId(Long employeeId);

    /**
     * 🔹 Verifica si existe un dependiente con un documento determinado.
     * Esto evita duplicados en los registros.
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * 🔹 (Opcional) Consulta personalizada para listar dependientes
     * junto con el nombre completo del empleado (optimiza las vistas del frontend).
     */
    @Query("""
        SELECT d FROM Dependent d 
        JOIN FETCH d.employee e
        ORDER BY e.firstName, e.lastName, d.fullName
    """)
    List<Dependent> findAllWithEmployee();
}
