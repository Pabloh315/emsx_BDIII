package com.app.emsx.dtos;

import com.app.emsx.entities.Dependent.Relationship;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DependentDTO {
    private Long id;
    private String fullName;
    private Relationship relationship;
    private LocalDate birthDate;
    private String documentNumber;
    private Boolean isStudent;
    private Integer coveragePercentage;

    // 🔹 Datos del empleado asociados
    private Long employeeId;
    private String employeeFullName; // <-- NUEVO campo
}
