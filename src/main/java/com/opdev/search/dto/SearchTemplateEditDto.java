package com.opdev.search.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchTemplateEditDto {

    @NonNull
    private Long id;
    @NonNull
    @NotEmpty
    private String name;

}
