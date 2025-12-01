package com.app.emsx.mappers;

import com.app.emsx.dtos.DependentDTO;
import com.app.emsx.dtos.EmployeeDTO;
import com.app.emsx.entities.Department;
import com.app.emsx.entities.Employee;
import com.app.emsx.entities.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmployeeMapper {

    /**
     * Converts an Employee entity to EmployeeDTO
     */
    public static EmployeeDTO mapEmployeeToEmployeeDTO(Employee employee) {
        if (employee == null) return null;

        // --- Dependents -> DTOs
        List<DependentDTO> dependentDTOs = null;
        if (employee.getDependents() != null) {
            dependentDTOs = employee.getDependents()
                    .stream()
                    .map(DependentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        // --- Skills -> IDs and names
        List<Long> skillIds = new ArrayList<>();
        List<String> skillNames = new ArrayList<>();
        if (employee.getSkills() != null) {
            for (Skill s : employee.getSkills()) {
                if (s != null) {
                    skillIds.add(s.getId());
                    skillNames.add(s.getName());
                }
            }
        }

        // --- Build DTO
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setAddress(employee.getAddress());
        dto.setPhone(employee.getPhone());

        // Department -> ID + Name
        Department dept = employee.getDepartment();
        if (dept != null) {
            dto.setDepartmentId(dept.getId());
            dto.setDepartmentName(dept.getName());
        }

        dto.setDependents(dependentDTOs);
        dto.setSkillIds(skillIds);
        dto.setSkillNames(skillNames);

        return dto;
    }

    /**
     * Converts EmployeeDTO to Employee entity.
     * Note: Department and Skills should be set in Service layer.
     */
    public static Employee mapEmployeeDTOToEmployee(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) return null;

        Employee e = new Employee();
        e.setId(employeeDTO.getId());
        e.setFirstName(employeeDTO.getFirstName());
        e.setLastName(employeeDTO.getLastName());
        e.setEmail(employeeDTO.getEmail());
        e.setAddress(employeeDTO.getAddress());
        e.setPhone(employeeDTO.getPhone());
        // Department is set in Service using departmentId

        // Dependents -> Entities (bidirectional)
        if (employeeDTO.getDependents() != null) {
            employeeDTO.getDependents().stream()
                    .filter(Objects::nonNull)
                    .map(DependentMapper::toEntity)
                    .forEach(e::addDependent);
        }

        // Skills will be handled in the Service layer using skillIds

        return e;
    }
}
