package com.opdev.position;

import com.opdev.exception.ApiSkillBadStatusException;
import com.opdev.model.talent.Position;
import com.opdev.model.talent.PositionSkill;
import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import com.opdev.repository.PositionSkillRepository;
import com.opdev.skill.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionSkillServiceImpl implements PositionSkillService {

    private final PositionSkillRepository repository;
    private final PositionService positionService;
    private final SkillService skillService;

    @Override
    @Transactional
    public List<PositionSkill> addSkillsToPosition(final String positionCode, final List<String> skillCodes) {
        Objects.requireNonNull(positionCode);
        Objects.requireNonNull(skillCodes);
        final List<PositionSkill> result = new ArrayList<>();
        final Position foundPosition = positionService.get(positionCode);
        skillCodes.forEach(skillCode -> {
            final Skill foundSkill = skillService.get(skillCode);
            checkSkillStatus(foundSkill);
            final PositionSkill created = repository.save(PositionSkill.builder().position(foundPosition).skill(foundSkill).build());
            result.add(created);
        });
        LOGGER.info("New relation(s) between position with code {} and skill code(s) {} has been created.", positionCode, skillCodes.toString());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionSkill> getSkillsByPosition(final String positionCode) {
        Objects.requireNonNull(positionCode);
        final Position foundPosition = positionService.get(positionCode);
        return repository.findByPosition(foundPosition);
    }

    @Override
    @Transactional
    public void remove(String positionCode, String skillCode) {
        Objects.requireNonNull(positionCode);
        Objects.requireNonNull(skillCode);
        final Position foundPosition = positionService.get(positionCode);
        final Skill foundSkill = skillService.get(skillCode);
        repository.deleteByPositionAndSkill(foundPosition, foundSkill);
        LOGGER.info("Relation with position code {} and skill code {} has been removed.", positionCode, skillCode);
    }

    private void checkSkillStatus(final Skill skill) {
        Objects.requireNonNull(skill);
        if (SkillStatus.PENDING.equals(skill.getStatus())) {
            throw new ApiSkillBadStatusException(skill.getCode());
        }
    }

}
