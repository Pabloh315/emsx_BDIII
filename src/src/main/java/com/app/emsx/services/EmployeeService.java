package com.app.emsx.services;

import com.app.emsx.dtos.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO updateEmployee(Long employeeID, EmployeeDTO employeeDTO);
    String deleteEmployee(Long employeeID);
    EmployeeDTO getEmployee(Long employeeID);
    List<EmployeeDTO> getEmployees();
}
