package com.opdev.skill;

import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SkillService {

    Skill add(final Skill newSkill);

    Skill get(final String code);

    Page<Skill> find(final Specification<Skill> skillSpec, final Pageable pageable);

    Skill edit(final Skill modified);

    void remove(final String code);

    Skill updateStatus(final String code, final SkillStatus status);

}
