package com.opdev.repository;

import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {

    Optional<Skill> findByCode(String code);

    Long deleteByCode(String code);

    Skill getByCode(String code);

    List<Skill> findAllByStatus(SkillStatus skillStatus);

}
