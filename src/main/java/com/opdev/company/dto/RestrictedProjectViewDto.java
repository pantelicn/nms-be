package com.opdev.company.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import com.opdev.model.talent.Project;

import lombok.Getter;

@Getter
public class RestrictedProjectViewDto {

    private String description;

    private String technologiesUsed;

    private String myRole;

    private Long duration;

    public RestrictedProjectViewDto(Project project) {
        description = project.getDescription();
        technologiesUsed = project.getTechnologiesUsed();
        myRole = project.getMyRole();
        if (project.getEndDate() != null) {
            duration = ChronoUnit.MONTHS.between(LocalDate.ofInstant(project.getStartDate(), ZoneOffset.UTC), LocalDate.ofInstant(project.getEndDate(), ZoneOffset.UTC));
        } else {
            duration = ChronoUnit.MONTHS.between(LocalDate.ofInstant(project.getStartDate(), ZoneOffset.UTC), LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC));
        }
    }

}
