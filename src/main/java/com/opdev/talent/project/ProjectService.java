package com.opdev.talent.project;

import java.time.Instant;

import com.opdev.model.talent.Project;

public interface ProjectService {

    Project create(String description, String technologiesUsed, String myRole, String talentUsername, Instant startDate, Instant endDate);

    Project edit(Long id, String description, String technologiesUsed, String myRole, String talentUsername, Instant startDate, Instant endDate);

    void remove(Long id, String talentUsername);

}
