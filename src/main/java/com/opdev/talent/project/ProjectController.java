package com.opdev.talent.project;

import static com.opdev.config.security.SpELAuthorizationExpressions.AS_MATCHING_TALENT;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.model.talent.Project;
import com.opdev.talent.dto.ProjectCreateDto;
import com.opdev.talent.dto.ProjectEditDto;
import com.opdev.talent.dto.ProjectViewDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/talents/{username}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    @PostMapping
    @PreAuthorize(AS_MATCHING_TALENT)
    public ResponseEntity<ProjectViewDto> create(@Valid @RequestBody ProjectCreateDto createDto, @PathVariable String username) {
        Project created = service.create(createDto.getDescription(), createDto.getTechnologiesUsed(), createDto.getMyRole(), username);
        return ResponseEntity.ok(new ProjectViewDto(created));
    }

    @PutMapping
    @PreAuthorize(AS_MATCHING_TALENT)
    public ResponseEntity<ProjectViewDto> edit(@Valid @RequestBody ProjectEditDto updateDto, @PathVariable String username) {
        Project updated = service.edit(updateDto.getId(), updateDto.getDescription(), updateDto.getTechnologiesUsed(), updateDto.getMyRole(), username);
        return ResponseEntity.ok(new ProjectViewDto(updated));
    }

    @DeleteMapping("{id}")
    @PreAuthorize(AS_MATCHING_TALENT)
    public void remove(@PathVariable Long id, @PathVariable String username) {
        service.remove(id, username);
    }

}
