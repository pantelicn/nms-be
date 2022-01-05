package com.opdev.company.dto;

import com.opdev.model.request.TalentTermRequestStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class TermCreateDto {

    private Long termId;

    private TalentTermRequestStatus status;

    private String counterOffer;

}
