package com.opdev.position;

import com.opdev.model.talent.PositionSkill;

import java.util.List;
import java.util.Set;

public interface PositionSkillService {

    List<PositionSkill> addSkillsToPosition(String positionCode, Set<String> skillCodes);

    List<PositionSkill> getSkillsByPosition(String positionCode);

    void remove(String positionCode, String skillCode);

}
