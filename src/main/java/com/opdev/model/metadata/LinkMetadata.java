package com.opdev.model.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString(callSuper = true)
public class LinkMetadata {

    String title;
    String description;
    String image;
    String url;

}