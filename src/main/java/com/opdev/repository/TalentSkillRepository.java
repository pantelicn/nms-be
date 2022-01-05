package com.opdev.repository;

import com.opdev.model.talent.Skill;
import com.opdev.model.talent.Talent;
import com.opdev.model.talent.TalentSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalentSkillRepository extends JpaRepository<TalentSkill, Long> {

    List<TalentSkill> findByTalent(Talent talent);
    Long deleteByTalentAndSkill(Talent talent, Skill skill);

}
