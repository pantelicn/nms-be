package com.opdev.position;

import com.opdev.config.security.Roles;
import com.opdev.model.talent.PositionSkill;
import com.opdev.position.dto.PositionSkillsViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("v1/positions/{code}/skills")
@RequiredArgsConstructor
public class PositionSkillController {

    private final PositionSkillService service;

    @PostMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<PositionSkillsViewDto> addSkills(@RequestBody Set<String> skillCodes, @PathVariable String code) {
        final List<PositionSkill> created = service.addSkillsToPosition(code, skillCodes);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PositionSkillsViewDto(created));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<PositionSkillsViewDto> getSkills(@PathVariable String code) {
        final List<PositionSkill> found = service.getSkillsByPosition(code);
        return ResponseEntity.ok(new PositionSkillsViewDto(found));
    }

    @DeleteMapping("/{skillCode}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<Void> remove(@PathVariable String code, @PathVariable String skillCode) {
        service.remove(code, skillCode);
        return ResponseEntity.noContent().build();
    }

}
