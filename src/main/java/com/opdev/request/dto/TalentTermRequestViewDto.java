package com.opdev.request.dto;

import com.opdev.model.request.TalentTermRequest;
import com.opdev.model.request.TalentTermRequestStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TalentTermRequestViewDto {

    private Long id;
    private String counterOffer;
    private TalentTermRequestStatus status;


    public TalentTermRequestViewDto(TalentTermRequest talentTermRequest) {
        id = talentTermRequest.getId();
        counterOffer = talentTermRequest.getCounterOffer();
        status = talentTermRequest.getStatus();
    }

}
