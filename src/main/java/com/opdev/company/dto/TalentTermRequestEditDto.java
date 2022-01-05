package com.opdev.company.dto;

import com.opdev.model.request.TalentTermRequestStatus;

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
public class TalentTermRequestEditDto {

    @NonNull
    private Long id;
    private String counterOffer;
    @NonNull
    private TalentTermRequestStatus status;

}
