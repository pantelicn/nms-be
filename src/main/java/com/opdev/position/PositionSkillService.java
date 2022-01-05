package com.opdev.position;

import com.opdev.model.talent.PositionSkill;

import java.util.List;

public interface PositionSkillService {

    List<PositionSkill> addSkillsToPosition(String positionCode, List<String> skillCodes);

    List<PositionSkill> getSkillsByPosition(String positionCode);

    void remove(String positionCode, String skillCode);

}
