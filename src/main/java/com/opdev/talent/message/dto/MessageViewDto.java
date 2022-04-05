package com.opdev.talent.message.dto;

import com.opdev.model.user.UserType;
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
    private String companyUsername;
    private UserType createdBy;
    private boolean seen;

}
