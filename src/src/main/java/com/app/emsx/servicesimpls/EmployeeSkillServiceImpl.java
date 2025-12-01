package com.app.emsx.servicesimpls;

import com.app.emsx.dtos.SkillDTO;
import com.app.emsx.entities.Employee;
import com.app.emsx.entities.Skill;
import com.app.emsx.exceptions.ResourceNotFoundException;
import com.app.emsx.mappers.SkillMapper;
import com.app.emsx.repositories.EmployeeRepository;
import com.app.emsx.repositories.SkillRepository;
import com.app.emsx.services.EmployeeSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class EmployeeSkillServiceImpl implements EmployeeSkillService {

    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> listSkills(Long employeeId) {
        Employee e = getEmployee(employeeId);
        return e.getSkills().stream().map(SkillMapper::toDTO).toList();
    }

    @Override
    public void addSkill(Long employeeId, Long skillId) {
        Employee e = getEmployee(employeeId);
        Skill s = getSkill(skillId);
        e.addSkill(s); // owning side: Employee
        employeeRepository.save(e);
    }

    @Override
    public void removeSkill(Long employeeId, Long skillId) {
        Employee e = getEmployee(employeeId);
        Skill s = getSkill(skillId);
        e.removeSkill(s);
        employeeRepository.save(e);
    }

    @Override
    public void replaceSkills(Long employeeId, List<Long> skillIds) {
        Employee e = getEmployee(employeeId);
        e.clearSkills();
        if (skillIds != null && !skillIds.isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            e.getSkills().addAll(new HashSet<>(skills));
        }
        employeeRepository.save(e);
    }

    private Employee getEmployee(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }
    private Skill getSkill(Long id){
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
    }
}
