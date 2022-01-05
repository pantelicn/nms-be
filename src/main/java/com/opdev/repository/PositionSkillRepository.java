package com.opdev.repository;

import com.opdev.model.talent.Position;
import com.opdev.model.talent.PositionSkill;
import com.opdev.model.talent.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionSkillRepository extends JpaRepository<PositionSkill, Long> {

    List<PositionSkill> findByPosition(Position position);

    Long deleteByPositionAndSkill(Position position, Skill skill);

}
