package com.app.emsx.mappers;

import com.app.emsx.dtos.DependentDTO;
import com.app.emsx.entities.Dependent;

public class DependentMapper {

    public static DependentDTO toDTO(Dependent d) {
        if (d == null) return null;

        DependentDTO dto = new DependentDTO();
        dto.setId(d.getId());
        dto.setFullName(d.getFullName());
        dto.setRelationship(d.getRelationship());
        dto.setBirthDate(d.getBirthDate());
        dto.setDocumentNumber(d.getDocumentNumber());
        dto.setIsStudent(d.getIsStudent());
        dto.setCoveragePercentage(d.getCoveragePercentage());

        // 🔹 Incluye datos del empleado (nombre y ID)
        if (d.getEmployee() != null) {
            dto.setEmployeeId(d.getEmployee().getId());
            dto.setEmployeeFullName(
                    d.getEmployee().getFirstName() + " " + d.getEmployee().getLastName()
            );
        }

        return dto;
    }

    public static Dependent toEntity(DependentDTO dto) {
        if (dto == null) return null;

        Dependent d = new Dependent();
        d.setId(dto.getId());
        d.setFullName(dto.getFullName());
        d.setRelationship(dto.getRelationship());
        d.setBirthDate(dto.getBirthDate());
        d.setDocumentNumber(dto.getDocumentNumber());
        d.setIsStudent(dto.getIsStudent() != null ? dto.getIsStudent() : false);
        d.setCoveragePercentage(dto.getCoveragePercentage() != null ? dto.getCoveragePercentage() : 0);

        return d;
    }
}
