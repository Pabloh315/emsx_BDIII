package com.app.emsx.servicesimpls;

import com.app.emsx.dtos.SkillDTO;
import com.app.emsx.entities.Skill;
import com.app.emsx.mappers.SkillMapper;
import com.app.emsx.repositories.SkillRepository;
import com.app.emsx.services.SkillService;
import com.app.emsx.exceptions.ResourceNotFoundException; // usa tu excepción
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public SkillDTO create(SkillDTO dto) {
        if (skillRepository.existsByNameIgnoreCase(dto.getName()))
            throw new IllegalArgumentException("Skill ya existe: " + dto.getName());
        Skill s = skillRepository.save(SkillMapper.toEntity(dto));
        return SkillMapper.toDTO(s);
    }

    @Override
    public SkillDTO update(Long id, SkillDTO dto) {
        Skill s = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        return SkillMapper.toDTO(s);
    }

    @Override
    public void delete(Long id) {
        Skill s = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
        skillRepository.delete(s);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillDTO getById(Long id) {
        Skill s = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
        return SkillMapper.toDTO(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> getAll() {
        return skillRepository.findAll().stream().map(SkillMapper::toDTO).toList();
    }
}
