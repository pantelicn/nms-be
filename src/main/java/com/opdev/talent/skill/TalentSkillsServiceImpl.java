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
        final List<PositionSkill> skillPositions = new ArrayList<>();
        skillCodes.forEach(skillCode -> {
            final Skill foundSkill = skillService.get(skillCode);
            final TalentSkill created = repository.save(TalentSkill.builder().skill(foundSkill).talent(foundTalent).build());
            skillPositions.addAll(foundSkill.getSkillPositions());
            result.add(created);
        });
        addPositionsToTalent(username, skillPositions);
        return result;
    }

    private void addPositionsToTalent(String username, List<PositionSkill> skillPositions) {
        List<Position> existingPositions = talentPositionService.getByTalent(username);
        List<Position> positions = skillPositions.stream()
                .map(PositionSkill::getPosition)
                .filter(position -> !positionExists(existingPositions, position))
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
        removePositionsFromTalent(username, foundSkill.getSkillPositions());
        repository.deleteByTalentAndSkill(foundTalent, foundSkill);
    }

    private void removePositionsFromTalent(String username, List<PositionSkill> skillPositionsToRemove) {
        String skillCode = skillPositionsToRemove.get(0).getSkill().getCode();
        List<Position> existingPositions = talentPositionService.getByTalent(username);
        List<TalentSkill> existingSkills = getSkillsByTalent(username);
        existingSkills.removeIf(existingSkill -> skillCode.equals(existingSkill.getSkill().getCode()));
        skillPositionsToRemove.removeIf(skillPosition -> skillsHavePosition(existingSkills, skillPosition));
        List<Position> positionsToRemove = skillPositionsToRemove.stream()
                .map(PositionSkill::getPosition)
                .filter(position -> positionExists(existingPositions, position))
                .collect(Collectors.toList());
        talentPositionService.removePositionsFromTalent(username, positionsToRemove);
    }

    private static boolean skillsHavePosition(List<TalentSkill> existingSkills, PositionSkill skillPosition) {
        List<Position> existingPositions = existingSkills.stream()
                .flatMap(existingSkill -> existingSkill.getSkill().getSkillPositions().stream())
                .map(PositionSkill::getPosition)
                .collect(Collectors.toList());

        return existingPositions.stream().anyMatch(position -> position.getCode().equals(skillPosition.getPosition().getCode()));
    }

    private Talent getTalent(final String username) {
        if (userService.isAdminLoggedIn()) {
            return talentService.getByUsername(username);
        } else {
            return talentService.getByUsername(userService.getLoggedInUser().getUsername());
        }
    }

    private static boolean positionExists(List<Position> existingPositions, Position position) {
        return existingPositions.stream().anyMatch(existingPosition -> existingPosition.getCode().equals(position.getCode()));
    }

}
