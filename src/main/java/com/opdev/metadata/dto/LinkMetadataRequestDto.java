package com.opdev.metadata.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LinkMetadataRequestDto {

    @NonNull
    @NotEmpty
    private String url;
    
}
