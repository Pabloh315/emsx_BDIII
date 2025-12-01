package com.app.emsx.services;

import com.app.emsx.dtos.SkillDTO;
import java.util.List;

public interface SkillService {
    SkillDTO create(SkillDTO dto);
    SkillDTO update(Long id, SkillDTO dto);
    void delete(Long id);
    SkillDTO getById(Long id);
    List<SkillDTO> getAll();
}
