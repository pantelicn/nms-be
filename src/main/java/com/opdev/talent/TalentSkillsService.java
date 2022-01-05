package com.opdev.talent;

import com.opdev.model.talent.TalentSkill;

import java.util.List;

public interface TalentSkillsService {

    List<TalentSkill> addSkillsToTalent(String username, List<String> skillCodes);

    List<TalentSkill> getSkillsByTalent(String username);

    void removeSkillFromTalent(String username, String skillCode);

}
