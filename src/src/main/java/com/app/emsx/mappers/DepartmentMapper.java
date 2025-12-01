package com.app.emsx.mappers;

import com.app.emsx.dtos.departmentDTO.DepartmentRequest;
import com.app.emsx.dtos.departmentDTO.DepartmentResponse;
import com.app.emsx.entities.Department;

/**
 * DepartmentMapper
 * -----------------------------------------------------
 * 🧩 Convierte entre entidades y DTOs de Department.
 * ✔ De Entity → Response
 * ✔ De Request → Entity
 * -----------------------------------------------------
 */
public class DepartmentMapper {

    /**
     * 🔹 Convierte una entidad Department a un DTO DepartmentResponse.
     */
    public static com.app.emsx.dtos.department.DepartmentDto toResponse(Department department) {
        if (department == null) return null;

        return com.app.emsx.dtos.department.DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription()) // ✅ ahora se incluye correctamente
                .build();
    }

    /**
     * 🔹 Convierte un DTO DepartmentRequest a una entidad Department.
     */
    public static Department toEntity(com.app.emsx.dtos.department.DepartmentDto request) {
        if (request == null) return null;

        return Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
