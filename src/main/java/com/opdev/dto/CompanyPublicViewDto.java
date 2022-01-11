package com.opdev.dto;

import com.opdev.model.company.Company;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@ToString(callSuper = true)
public class CompanyPublicViewDto {

    private Long id;

    private String name;

    private String description;

    public CompanyPublicViewDto(final Company company) {
        this.asView(company);
    }

    private void asView(@NonNull final Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.description = company.getDescription();
    }

}
