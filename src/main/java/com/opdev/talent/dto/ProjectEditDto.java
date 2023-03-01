package com.opdev.talent.dto;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ProjectEditDto {

    @NonNull
    private Long id;

    @NonNull
    private String description;

    @NonNull
    private String technologiesUsed;

    @NonNull
    private String myRole;

    @NonNull
    private Instant startDate;

    private Instant endDate;

}
