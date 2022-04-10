package com.opdev.request.dto;

import com.opdev.company.dto.TalentTermRequestEditDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class RequestResponseDto {

    @NonNull
    private Long requestId;

    @NonNull
    private TalentTermRequestEditDto newTermRequest;

    @NonNull
    private Instant modifiedOn;

}
