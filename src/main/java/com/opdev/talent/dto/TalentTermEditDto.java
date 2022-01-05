package com.opdev.talent.dto;

import com.opdev.model.term.TalentTerm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TalentTermEditDto {

    @NonNull
    @NotNull
    private Long id;

    @NonNull
    @NotNull
    private String value;

    @NonNull
    @NotNull
    private Boolean negotiable;

    public TalentTerm asTalentTerm() {
        return TalentTerm.builder().id(id).value(value).negotiable(negotiable).build();
    }

}
