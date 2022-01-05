package com.opdev.company.dto;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

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

    @NotBlank
    private String address1;

    private String address2;

    public Company asCompany(final Company oldCompany, final User admin) {
        Objects.requireNonNull(oldCompany);

        final CompanyBuilder oldCompanyBuilder = oldCompany.toBuilder();

        if (MappingUtils.shouldUpdate(name, oldCompany.getName())) {
            oldCompanyBuilder.name(name);
        }
        if (MappingUtils.shouldUpdate(description, oldCompany.getDescription())) {
            oldCompanyBuilder.description(description);
        }
        if (MappingUtils.shouldUpdate(address1, oldCompany.getAddress1())) {
            oldCompanyBuilder.address1(address1);
        }
        oldCompanyBuilder.address2(address2);

        final Company updatedCompany = oldCompanyBuilder.build();
        if (null != admin) {
            updatedCompany.setModifiedBy(admin);
        } else {
            updatedCompany.setModifiedBy(oldCompany.getUser());
        }
        return updatedCompany;
    }

}
