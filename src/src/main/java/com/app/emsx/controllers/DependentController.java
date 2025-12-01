package com.app.emsx.controllers;

import com.app.emsx.dtos.DependentDTO;
import com.app.emsx.services.DependentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DependentController {

    private final DependentService dependentService;

    // ============================================================
    // 🔹 LISTAR TODOS LOS DEPENDIENTES (para /api/dependents)
    // ============================================================
    @GetMapping("/dependents")
    public ResponseEntity<List<DependentDTO>> listAll() {
        List<DependentDTO> list = dependentService.listByEmployee(null);
        return ResponseEntity.ok(list);
    }

    // ============================================================
    // 🔹 LISTAR DEPENDIENTES DE UN EMPLEADO (para /api/employees/{employeeId}/dependents)
    // ============================================================
    @GetMapping("/employees/{employeeId}/dependents")
    public ResponseEntity<List<DependentDTO>> listByEmployee(@PathVariable Long employeeId) {
        List<DependentDTO> list = dependentService.listByEmployee(employeeId);
        return ResponseEntity.ok(list);
    }

    // ============================================================
    // 🔹 OBTENER UN DEPENDIENTE POR ID
    // ============================================================
    @GetMapping("/dependents/{dependentId}")
    public ResponseEntity<DependentDTO> getById(@PathVariable Long dependentId) {
        DependentDTO dto = dependentService.getById(dependentId);
        return ResponseEntity.ok(dto);
    }

    // ============================================================
    // 🔹 AGREGAR UN NUEVO DEPENDIENTE
    // ============================================================
    @PostMapping("/dependents")
    public ResponseEntity<DependentDTO> create(@RequestBody DependentDTO dto) {
        if (dto.getEmployeeId() == null) {
            return ResponseEntity.badRequest().build();
        }
        DependentDTO created = dependentService.addToEmployee(dto.getEmployeeId(), dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ============================================================
    // 🔹 ACTUALIZAR UN DEPENDIENTE EXISTENTE
    // ============================================================
    @PutMapping("/dependents/{dependentId}")
    public ResponseEntity<DependentDTO> update(@PathVariable Long dependentId, @RequestBody DependentDTO dto) {
        if (dto.getEmployeeId() == null) {
            return ResponseEntity.badRequest().build();
        }
        DependentDTO updated = dependentService.update(dto.getEmployeeId(), dependentId, dto);
        return ResponseEntity.ok(updated);
    }

    // ============================================================
    // 🔹 ELIMINAR UN DEPENDIENTE
    // ============================================================
    @DeleteMapping("/dependents/{dependentId}")
    public ResponseEntity<String> delete(@PathVariable Long dependentId, @RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            dependentService.remove(employeeId, dependentId);
        } else {
            // fallback en caso de no pasar employeeId
            dependentService.deleteById(dependentId);
        }
        return ResponseEntity.ok("Dependent deleted successfully!");
    }
}
