package com.app.emsx.mappers;

import com.app.emsx.dtos.SkillDTO;
import com.app.emsx.entities.Skill;

public class SkillMapper {
    public static SkillDTO toDTO(Skill s){
        if (s == null) return null;
        return new SkillDTO(s.getId(), s.getName(), s.getDescription());
    }
    public static Skill toEntity(SkillDTO dto){
        if (dto == null) return null;
        Skill s = new Skill();
        s.setId(dto.getId());
        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        return s;
    }
}
