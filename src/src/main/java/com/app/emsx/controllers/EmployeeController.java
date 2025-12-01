package com.app.emsx.controllers;

import com.app.emsx.dtos.EmployeeDTO;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*") // permite llamadas desde el frontend (Vite)
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 🔹 Create new employee
     * POST: /api/employees
     */
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO savedEmployeeDTO = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployeeDTO);
    }

    /**
     * 🔹 Get all employees
     * GET: /api/employees
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * 🔹 Get a single employee by ID
     * GET: /api/employees/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employeeDTO = employeeService.getEmployee(id);
        return ResponseEntity.ok(employeeDTO);
    }

    /**
     * 🔹 Update an existing employee
     * PUT: /api/employees/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeDTO employeeDTO
    ) {
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * 🔹 Delete an employee by ID
     * DELETE: /api/employees/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully.");
    }

    /**
     * ⚠️ Optional: Global handling of ResourceNotFoundException (nice frontend errors)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
