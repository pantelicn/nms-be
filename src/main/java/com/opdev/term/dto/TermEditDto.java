package com.opdev.term.dto;

import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@Getter
public class TermEditDto {

    @NonNull
    @NotEmpty
    private String code;

    @NonNull
    @NotEmpty
    private String name;

    @NonNull
    @NotEmpty
    private String description;

    @NonNull
    private TermType type;

    private boolean availableForSearch;

    public Term asTerm() {
        return Term.builder()
                .name(name)
                .description(description)
                .type(type)
                .code(code)
                .availableForSearch(availableForSearch)
                .build();
    }

}
