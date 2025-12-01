package com.app.emsx.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "dependents",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_dependent_document", columnNames = {"document_number"})
        })
public class Dependent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Relationship relationship;

    private LocalDate birthDate;

    @Column(name = "document_number", nullable = false, length = 40)
    private String documentNumber;

    private Boolean isStudent = false;

    private Integer coveragePercentage = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dependent_employee"))
    @JsonBackReference
    private Employee employee;

    public enum Relationship { CHILD, SPOUSE, PARENT, OTHER }
}
