package com.opdev.talent.dto;

import com.opdev.model.term.TalentTerm;
import com.opdev.term.dto.TermViewDto;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class TalentTermViewDto {

    private Long id;

    private String value;

    private Boolean negotiable;

    private TermViewDto term;

    public TalentTermViewDto(TalentTerm talentTerm) {
        id = talentTerm.getId();
        value = talentTerm.getValue();
        negotiable = talentTerm.getNegotiable();
        term = new TermViewDto(talentTerm.getTerm());
    }

}
