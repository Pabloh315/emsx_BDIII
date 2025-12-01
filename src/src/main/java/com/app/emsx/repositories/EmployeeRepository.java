package com.app.emsx.repositories;

import com.app.emsx.entities.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * ✅ Carga las relaciones department y skills en una sola consulta.
     * Esto evita LazyInitializationException y ConcurrentModificationException.
     */
    @Override
    @EntityGraph(attributePaths = {"department", "skills"})
    List<Employee> findAll();

    @Query("SELECT e.department.name, COUNT(e) FROM Employee e GROUP BY e.department.name")
    List<Object[]> countEmployeesByDepartment();

}
