package com.opdev.request.dto;

import com.opdev.company.dto.TalentTermRequestEditDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class RequestResponseDto {

    @NonNull
    private Long requestId;

    @NonNull
    private List<TalentTermRequestEditDto> newTermRequests;

    @NonNull
    private Instant modifiedOn;

}
