package com.app.emsx.controllers;

import com.app.emsx.dtos.SkillDTO;
import com.app.emsx.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillDTO> create(@RequestBody SkillDTO dto){
        return new ResponseEntity<>(skillService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> update(@PathVariable Long id, @RequestBody SkillDTO dto){
        return ResponseEntity.ok(skillService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(skillService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAll(){
        return ResponseEntity.ok(skillService.getAll());
    }
}
