package com.app.emsx.servicesimpls;

import com.app.emsx.dtos.DependentDTO;
import com.app.emsx.entities.Dependent;
import com.app.emsx.entities.Employee;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.DependentMapper;
import com.app.emsx.repositories.DependentRepository;
import com.app.emsx.repositories.EmployeeRepository;
import com.app.emsx.services.DependentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DependentServiceImpl implements DependentService {

    private final EmployeeRepository employeeRepository;
    private final DependentRepository dependentRepository;

    // ===============================================================
    // 🔹 List all dependents (para /api/dependents)
    // ===============================================================
    @Override
    @Transactional(readOnly = true)
    public List<DependentDTO> listAll() {
        return dependentRepository.findAllWithEmployee()
                .stream()
                .map(DependentMapper::toDTO)
                .collect(Collectors.toList());
    }


    // ===============================================================
    // 🔹 List dependents by employee (para /api/employees/{id}/dependents)
    // ===============================================================
    @Override
    @Transactional(readOnly = true)
    public List<DependentDTO> listByEmployee(Long employeeId) {
        // Si employeeId es null, devuelve todos
        if (employeeId == null) {
            return listAll();
        }

        verifyEmployee(employeeId);

        return dependentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(DependentMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================================================
    // 🔹 Add dependent to employee
    // ===============================================================
    @Override
    public DependentDTO addToEmployee(Long employeeId, DependentDTO dto) {
        Employee employee = verifyEmployee(employeeId);

        if (dto.getDocumentNumber() != null &&
                dependentRepository.existsByDocumentNumber(dto.getDocumentNumber())) {
            throw new IllegalArgumentException("El documento ya existe para otro dependiente.");
        }

        Dependent dependent = DependentMapper.toEntity(dto);
        dependent.setEmployee(employee);

        Dependent saved = dependentRepository.save(dependent);
        return DependentMapper.toDTO(saved);
    }

    // ===============================================================
    // 🔹 Get dependent by ID
    // ===============================================================
    @Override
    @Transactional(readOnly = true)
    public DependentDTO getById(Long dependentId) {
        Dependent dependent = dependentRepository.findById(dependentId)
                .orElseThrow(() -> new ResourceNotFoundException("Dependent no encontrado con id=" + dependentId));
        return DependentMapper.toDTO(dependent);
    }

    // ===============================================================
    // 🔹 Update dependent (by employee and dependent ID)
    // ===============================================================
    @Override
    public DependentDTO update(Long employeeId, Long dependentId, DependentDTO dto) {
        Employee employee = verifyEmployee(employeeId);

        Dependent dependent = dependentRepository.findById(dependentId)
                .orElseThrow(() -> new ResourceNotFoundException("Dependent no encontrado con id=" + dependentId));

        if (!dependent.getEmployee().getId().equals(employee.getId())) {
            throw new IllegalArgumentException("El dependiente no pertenece a este empleado.");
        }

        dependent.setFullName(dto.getFullName());
        dependent.setRelationship(dto.getRelationship());
        dependent.setBirthDate(dto.getBirthDate());
        dependent.setDocumentNumber(dto.getDocumentNumber());
        dependent.setIsStudent(dto.getIsStudent() != null ? dto.getIsStudent() : false);
        dependent.setCoveragePercentage(dto.getCoveragePercentage() != null ? dto.getCoveragePercentage() : 0);

        Dependent updated = dependentRepository.save(dependent);
        return DependentMapper.toDTO(updated);
    }

    // ===============================================================
    // 🔹 Remove dependent (nested route)
    // ===============================================================
    @Override
    public void remove(Long employeeId, Long dependentId) {
        Employee employee = verifyEmployee(employeeId);

        Dependent dependent = dependentRepository.findById(dependentId)
                .orElseThrow(() -> new ResourceNotFoundException("Dependent no encontrado con id=" + dependentId));

        if (!dependent.getEmployee().getId().equals(employee.getId())) {
            throw new IllegalArgumentException("El dependiente no pertenece a este empleado.");
        }

        dependentRepository.delete(dependent);
    }

    // ===============================================================
    // 🔹 Delete dependent directly by ID (for /api/dependents/{id})
    // ===============================================================
    @Override
    public void deleteById(Long dependentId) {
        if (!dependentRepository.existsById(dependentId)) {
            throw new ResourceNotFoundException("Dependent no encontrado con id=" + dependentId);
        }
        dependentRepository.deleteById(dependentId);
    }

    // ===============================================================
    // 🔹 Helper method: verify employee existence
    // ===============================================================
    private Employee verifyEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee no encontrado con id=" + employeeId));
    }
}
