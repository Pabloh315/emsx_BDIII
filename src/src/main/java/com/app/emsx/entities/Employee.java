package com.app.emsx.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"dependents", "skills", "department"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;

    /**
     * Department (Many Employees → One Department)
     * Usamos FetchType.LAZY para evitar consultas innecesarias.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_employee_department"))
    private Department department;

    /**
     * Dependents (One Employee → Many Dependents)
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Dependent> dependents = new ArrayList<>();

    public void addDependent(Dependent dependent) {
        dependents.add(dependent);
        dependent.setEmployee(this);
    }

    public void removeDependent(Dependent dependent) {
        dependents.remove(dependent);
        dependent.setEmployee(null);
    }

    /**
     * Skills (Many Employees ↔ Many Skills)
     * No usar cascade REMOVE para no eliminar skills globalmente.
     * ⚠️ No incluir esta colección en equals/hashCode para evitar ConcurrentModificationException.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "employee_skills",
            joinColumns = @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "fk_emp_skill_employee")),
            inverseJoinColumns = @JoinColumn(name = "skill_id", foreignKey = @ForeignKey(name = "fk_emp_skill_skill"))
    )
    private Set<Skill> skills = new HashSet<>();

    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }

    public void clearSkills() {
        this.skills.clear();
    }
}
