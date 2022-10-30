package com.opdev.talent.skill;

import com.opdev.model.talent.TalentSkill;
import com.opdev.skill.dto.SkillViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.opdev.config.security.SpELAuthorizationExpressions.AS_MATCHING_TALENT_OR_ADMIN;

@RestController
@RequestMapping("v1/talents/{username}/skills")
@RequiredArgsConstructor
public class TalentSkillController {

    private final TalentSkillsService service;

    @PostMapping
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public List<SkillViewDto> addSkill(@RequestBody List<String> skillCodes, @PathVariable String username) {
        List<TalentSkill> created = service.addSkillsToTalent(username, skillCodes);
        return created.stream().map(TalentSkill::getSkill).map(SkillViewDto::new).collect(Collectors.toList());
    }

    @GetMapping
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public List<SkillViewDto> getSkills(@PathVariable String username) {
        List<TalentSkill> found = service.getSkillsByTalent(username);
        return found.stream().map(TalentSkill::getSkill).map(SkillViewDto::new).collect(Collectors.toList());
    }

    @DeleteMapping("/{skillCode}")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public void removeSkill(@PathVariable String username, @PathVariable String skillCode) {
        service.removeSkillFromTalent(username, skillCode);
    }

}
