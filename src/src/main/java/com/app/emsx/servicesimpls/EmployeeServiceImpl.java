package com.app.emsx.servicesimpls;

import com.app.emsx.dtos.EmployeeDTO;
import com.app.emsx.entities.Department;
import com.app.emsx.entities.Employee;
import com.app.emsx.entities.Skill;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.DependentMapper;
import com.app.emsx.mappers.EmployeeMapper;
import com.app.emsx.repositories.DepartmentRepository;
import com.app.emsx.repositories.EmployeeRepository;
import com.app.emsx.repositories.SkillRepository;
import com.app.emsx.services.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final SkillRepository skillRepository;

    /**
     * 🔹 Create Employee
     */
    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = EmployeeMapper.mapEmployeeDTOToEmployee(employeeDTO);

        // Set Department
        Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found with id: " + employeeDTO.getDepartmentId()));
        employee.setDepartment(department);

        // Set Skills (if provided)
        if (employeeDTO.getSkillIds() != null && !employeeDTO.getSkillIds().isEmpty()) {
            Set<Skill> skills = employeeDTO.getSkillIds().stream()
                    .map(skillId -> skillRepository.findById(skillId)
                            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId)))
                    .collect(Collectors.toSet());
            employee.setSkills(skills);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapEmployeeToEmployeeDTO(savedEmployee);
    }

    /**
     * 🔹 Update Employee
     */
    @Override
    public EmployeeDTO updateEmployee(Long employeeID, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(employeeID)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeID));

        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhone(employeeDTO.getPhone());
        employee.setAddress(employeeDTO.getAddress());

        // Update Department
        Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found with id: " + employeeDTO.getDepartmentId()));
        employee.setDepartment(department);

        // Update Skills
        employee.getSkills().clear();
        if (employeeDTO.getSkillIds() != null && !employeeDTO.getSkillIds().isEmpty()) {
            Set<Skill> skills = employeeDTO.getSkillIds().stream()
                    .map(skillId -> skillRepository.findById(skillId)
                            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId)))
                    .collect(Collectors.toSet());
            employee.setSkills(skills);
        }

        // Replace Dependents
        employee.getDependents().forEach(d -> d.setEmployee(null));
        employee.getDependents().clear();

        if (employeeDTO.getDependents() != null) {
            employeeDTO.getDependents().forEach(depDto ->
                    employee.addDependent(DependentMapper.toEntity(depDto))
            );
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapEmployeeToEmployeeDTO(updatedEmployee);
    }

    /**
     * 🔹 Delete Employee
     */
    @Override
    public String deleteEmployee(Long employeeID) {
        Employee employee = employeeRepository.findById(employeeID)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeID));
        employeeRepository.delete(employee);
        return "Employee has been deleted";
    }

    /**
     * 🔹 Get Single Employee by ID
     */
    @Override
    @Transactional
    public EmployeeDTO getEmployee(Long employeeID) {
        Employee employee = employeeRepository.findById(employeeID)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeID));

        return EmployeeMapper.mapEmployeeToEmployeeDTO(employee);
    }

    /**
     * 🔹 Get All Employees (optimized with EntityGraph)
     */
    @Override
    @Transactional
    public List<EmployeeDTO> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        // Ya no es necesario emp.getSkills().size(), Hibernate carga todo por @EntityGraph
        return employees.stream()
                .map(EmployeeMapper::mapEmployeeToEmployeeDTO)
                .collect(Collectors.toList());
    }
}
