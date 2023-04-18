package com.opdev.request.dto;

import com.opdev.model.request.TalentTermRequest;
import com.opdev.model.request.TalentTermRequestStatus;
import com.opdev.model.term.TermType;
import com.opdev.model.term.UnitOfMeasure;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TalentTermRequestViewDto {

    private Long id;
    private String name;
    private UnitOfMeasure unitOfMeasure;
    private String value;
    private String counterOffer;
    private TalentTermRequestStatus status;
    private TermType termType;

    public TalentTermRequestViewDto(TalentTermRequest talentTermRequest) {
        id = talentTermRequest.getId();
        name = talentTermRequest.getTalentTermSnapshot().getTermName();
        unitOfMeasure = talentTermRequest.getTalentTermSnapshot().getUnitOfMeasure();
        value = talentTermRequest.getTalentTermSnapshot().getValue();
        counterOffer = talentTermRequest.getCounterOffer();
        status = talentTermRequest.getStatus();
        termType = talentTermRequest.getTalentTermSnapshot().getTermType();
    }

}
