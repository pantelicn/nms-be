package com.opdev.talent.dto;

import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.Term;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TalentTermAddDto {

    @NonNull
    @NotNull
    private String value;

    @NonNull
    @NotNull
    private Boolean negotiable;

    @NonNull
    @NotBlank
    private String code;

    public TalentTerm asTalentTerm() {
        return TalentTerm.builder()
                .value(value)
                .negotiable(negotiable)
                .term(new Term(code)).build();
    }

}
