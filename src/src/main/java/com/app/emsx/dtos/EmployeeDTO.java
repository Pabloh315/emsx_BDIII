package com.app.emsx.dtos;

import com.app.emsx.dtos.DependentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;

    private Long departmentId;
    private String departmentName; // ✅ NEW FIELD

    private List<DependentDTO> dependents;

    private List<Long> skillIds;
    private List<String> skillNames; // ✅ NEW FIELD
}
