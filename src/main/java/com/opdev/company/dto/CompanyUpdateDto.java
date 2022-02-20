package com.opdev.company.dto;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.opdev.common.utils.MappingUtils;
import com.opdev.model.company.Company;
import com.opdev.model.company.Company.CompanyBuilder;
import com.opdev.model.user.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class CompanyUpdateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private UpdateCompanyLocationDto newLocation;

    public Company asCompany(final Company oldCompany, final User admin) {
        Objects.requireNonNull(oldCompany);

        final CompanyBuilder oldCompanyBuilder = oldCompany.toBuilder();

        if (MappingUtils.shouldUpdate(name, oldCompany.getName())) {
            oldCompanyBuilder.name(name);
        }
        if (MappingUtils.shouldUpdate(description, oldCompany.getDescription())) {
            oldCompanyBuilder.description(description);
        }
        if (oldCompany.getLocation().getId().equals(newLocation.getId())) {
            oldCompanyBuilder.location(newLocation.asCompanyLocation());
        }

        final Company updatedCompany = oldCompanyBuilder.build();
        if (null != admin) {
            updatedCompany.setModifiedBy(admin);
        } else {
            updatedCompany.setModifiedBy(oldCompany.getUser());
        }
        return updatedCompany;
    }

}
