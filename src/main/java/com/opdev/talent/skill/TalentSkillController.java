package com.opdev.talent.skill;

import com.opdev.config.security.Roles;
import com.opdev.model.talent.TalentSkill;
import com.opdev.talent.dto.TalentSkillsViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/talents/{username}/skills")
@RequiredArgsConstructor
public class TalentSkillController {

    private final TalentSkillsService service;

    @PostMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "') or (#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public ResponseEntity<TalentSkillsViewDto> addSkills(@RequestBody List<String> skillCodes, @PathVariable String username) {
        final List<TalentSkill> created = service.addSkillsToTalent(username, skillCodes);
        return ResponseEntity.ok(new TalentSkillsViewDto(created));
    }

    @GetMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "') or (#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public ResponseEntity<TalentSkillsViewDto> getSkills(@PathVariable String username) {
        final List<TalentSkill> found = service.getSkillsByTalent(username);
        return ResponseEntity.ok(new TalentSkillsViewDto(found));
    }

    @DeleteMapping("/{skillCode}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "') or (#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public ResponseEntity<Void> removeSkill(@PathVariable String username, @PathVariable String skillCode) {
        service.removeSkillFromTalent(username, skillCode);
        return ResponseEntity.noContent().build();
    }

}
