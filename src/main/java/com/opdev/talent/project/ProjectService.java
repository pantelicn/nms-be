package com.opdev.talent.project;

import com.opdev.model.talent.Project;

public interface ProjectService {

    Project create(String description, String technologiesUsed, String myRole, String talentUsername);

    Project edit(Long id, String description, String technologiesUsed, String myRole, String talentUsername);

    void remove(Long id, String talentUsername);

}
