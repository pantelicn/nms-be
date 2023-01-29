package com.opdev.talent.dto;

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
public class ProjectCreateDto {

    @NonNull
    private String description;

    @NonNull
    private String technologiesUsed;

    @NonNull
    private String myRole;

}
