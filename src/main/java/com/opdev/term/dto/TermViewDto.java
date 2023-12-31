package com.opdev.term.dto;

import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class TermViewDto {

    private Long id;
    private String name;
    private String code;
    private String description;
    private TermType type;
    private boolean availableForSearch;

    public TermViewDto(Term term) {
        id = term.getId();
        name = term.getName();
        code = term.getCode();
        description = term.getDescription();
        type = term.getType();
        availableForSearch = term.isAvailableForSearch();
    }
}
