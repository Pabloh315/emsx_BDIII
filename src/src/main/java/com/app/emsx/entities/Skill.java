package com.app.emsx.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "skills",
        uniqueConstraints = @UniqueConstraint(name = "uk_skill_name", columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "employees")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    /**
     * Inverse side of ManyToMany.
     * No incluir esta colección en equals/hashCode (⚠️ evitar ciclos y concurrencia).
     */
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Employee> employees = new HashSet<>();
}
