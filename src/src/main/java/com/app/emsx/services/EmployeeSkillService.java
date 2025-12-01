package com.app.emsx.services;

import com.app.emsx.dtos.SkillDTO;
import java.util.List;

public interface EmployeeSkillService {
    List<SkillDTO> listSkills(Long employeeId);
    void addSkill(Long employeeId, Long skillId);
    void removeSkill(Long employeeId, Long skillId);
    void replaceSkills(Long employeeId, List<Long> skillIds); // reemplazo total
}
