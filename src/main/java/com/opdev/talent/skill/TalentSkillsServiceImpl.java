package com.opdev.talent.skill;

import com.opdev.model.talent.Position;
import com.opdev.model.talent.PositionSkill;
import com.opdev.model.talent.Skill;
import com.opdev.model.talent.Talent;
import com.opdev.model.talent.TalentSkill;
import com.opdev.repository.TalentSkillRepository;
import com.opdev.skill.SkillService;
import com.opdev.talent.TalentService;
import com.opdev.talent.position.TalentPositionService;
import com.opdev.user.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TalentSkillsServiceImpl implements TalentSkillsService {

    private final TalentSkillRepository repository;
    private final UserService userService;
    private final TalentService talentService;
    private final SkillService skillService;
    private final TalentPositionService talentPositionService;

    @Override
    @Transactional
    public List<TalentSkill> addSkillsToTalent(final String username, final List<String> skillCodes) {
        Objects.requireNonNull(skillCodes);
        Objects.requireNonNull(username);
        final Talent foundTalent = getTalent(username);
        final List<TalentSkill> result = new ArrayList<>();
        skillCodes.forEach(skillCode -> {
            final Skill foundSkill = skillService.get(skillCode);
            final TalentSkill created = repository.save(TalentSkill.builder().skill(foundSkill).talent(foundTalent).build());
            addPositionsToTalent(username, foundSkill.getSkillPositions());
            result.add(created);
        });
        return result;
    }

    private void addPositionsToTalent(String username, List<PositionSkill> skillPositions) {
        List<Position> positions = skillPositions.stream()
                .map(PositionSkill::getPosition)
                .collect(Collectors.toList());
        talentPositionService.addPositionsToTalent(username, positions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TalentSkill> getSkillsByTalent(final String username) {
        Objects.requireNonNull(username);
        final Talent foundTalent = talentService.getByUsername(username);
        return repository.findByTalent(foundTalent);
    }

    @Override
    @Transactional
    public void removeSkillFromTalent(final String username, final String skillCode) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(skillCode);
        final Talent foundTalent = getTalent(username);
        final Skill foundSkill = skillService.get(skillCode);
        repository.deleteByTalentAndSkill(foundTalent, foundSkill);
    }

    private Talent getTalent(final String username) {
        if (userService.isAdminLoggedIn()) {
            return talentService.getByUsername(username);
        } else {
            return talentService.getByUsername(userService.getLoggedInUser().getUsername());
        }
    }

}
