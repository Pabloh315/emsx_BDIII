package com.app.emsx.services;

import com.app.emsx.dtos.DependentDTO;
import java.util.List;

public interface DependentService {

    // 🔹 Listar todos los dependents
    List<DependentDTO> listAll();

    // 🔹 Listar dependents por empleado
    List<DependentDTO> listByEmployee(Long employeeId);

    // 🔹 Agregar dependent a un empleado
    DependentDTO addToEmployee(Long employeeId, DependentDTO dto);

    // 🔹 Obtener dependiente por ID
    DependentDTO getById(Long dependentId);

    // 🔹 Actualizar dependent
    DependentDTO update(Long employeeId, Long dependentId, DependentDTO dto);

    // 🔹 Eliminar dependent de un empleado
    void remove(Long employeeId, Long dependentId);

    // 🔹 Eliminar dependiente por ID directo
    void deleteById(Long dependentId);
}
