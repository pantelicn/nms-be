package com.opdev.talent.dto;

import java.time.Instant;

import com.opdev.model.talent.Project;

import lombok.Getter;

@Getter
public class ProjectViewDto {

    private Long id;

    private String description;

    private String technologiesUsed;

    private String myRole;

    private Instant startDate;

    private Instant endDate;

    public ProjectViewDto(Project model) {
        id = model.getId();
        description = model.getDescription();
        technologiesUsed = model.getTechnologiesUsed();
        myRole = model.getMyRole();
        startDate = model.getStartDate();
        endDate = model.getEndDate();
    }

}
