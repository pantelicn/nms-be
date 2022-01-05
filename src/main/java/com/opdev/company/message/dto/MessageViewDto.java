package com.opdev.company.message.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MessageViewDto {

    private Long id;
    private String content;
    private String talentUsername;
    private boolean seen;

}
