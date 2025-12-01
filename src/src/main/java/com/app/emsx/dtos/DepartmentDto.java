package com.app.emsx.dtos.department;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {
    private Long id;
    private String name;
    private String description; // 👈 Agrega este campo
}
