package com.opdev.benefit.dto;

import com.opdev.model.company.Benefit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BenefitViewDto {

    private Long id;
    private String name;
    private String description;
    private Boolean isDefault;

    public BenefitViewDto(final Benefit benefit) {
        id = benefit.getId();
        name = benefit.getName();
        description = benefit.getDescription();
        isDefault = benefit.getIsDefault();
    }

}
