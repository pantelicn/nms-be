package com.opdev.skill;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.model.talent.Skill;
import com.opdev.skill.dto.SkillAddDto;
import com.opdev.skill.dto.SkillEditDto;
import com.opdev.skill.dto.SkillStatusDto;
import com.opdev.skill.dto.SkillViewDto;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/skills")
public class SkillController {

    private final SkillService service;

    @PostMapping
    @PreAuthorize(SpELAuthorizationExpressions.IS_AUTHENTICATED)
    public ResponseEntity<SkillViewDto> add(@Valid @RequestBody final SkillAddDto newSkill) {
        final Skill created = service.add(newSkill.asSkill());
        return ResponseEntity.status(HttpStatus.CREATED).body(new SkillViewDto(created));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<SkillViewDto> find(
            @Spec(path = "status", spec = Equal.class) Specification<Skill> skillSpec, final Pageable pageable) {
        return service.find(skillSpec, pageable).map(SkillViewDto::new);
    }

    @PutMapping
    @PreAuthorize(SpELAuthorizationExpressions.IS_ADMIN)
    public ResponseEntity<SkillViewDto> edit(@Valid @RequestBody final SkillEditDto modified) {
        final Skill updated = service.edit(modified.asSkill());
        return ResponseEntity.ok(new SkillViewDto(updated));
    }

    @GetMapping("/{code}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SkillViewDto> get(@PathVariable final String code) {
        final Skill found = service.get(code);
        return ResponseEntity.ok(new SkillViewDto(found));
    }

    @DeleteMapping("/{code}")
    @PreAuthorize(SpELAuthorizationExpressions.IS_ADMIN)
    public ResponseEntity<Void> remove(@PathVariable final String code) {
        service.remove(code);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{code}/status")
    @PreAuthorize(SpELAuthorizationExpressions.IS_ADMIN)
    public ResponseEntity<SkillViewDto> setStatus(@Valid @RequestBody final SkillStatusDto statusDto,
                                                  @PathVariable final String code) {
        final Skill modified = service.updateStatus(code, statusDto.getStatus());
        return ResponseEntity.ok(new SkillViewDto(modified));
    }

}
