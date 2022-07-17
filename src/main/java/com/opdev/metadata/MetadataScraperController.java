package com.opdev.metadata;

import com.opdev.metadata.dto.LinkMetadataRequestDto;
import com.opdev.model.metadata.LinkMetadata;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/link-preview")
@AllArgsConstructor
public class MetadataScraperController {

    private final MetadataScraperService metadataScraperService;

    @PostMapping
    @PreAuthorize("permitAll()")
    public LinkMetadata extract(@RequestBody @Valid LinkMetadataRequestDto linkMetadataRequestDto) {
        return metadataScraperService.extract(linkMetadataRequestDto.getUrl());
    }

}
