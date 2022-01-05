package com.opdev.company.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class RequestCreateDto {

    @NonNull
    private String talentId;
    @NonNull
    private String note;

    private List<TermCreateDto> terms;

}
