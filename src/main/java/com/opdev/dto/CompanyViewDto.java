package com.opdev.dto;

import java.util.Objects;

import com.opdev.model.company.Company;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@ToString(callSuper = true)
public class CompanyViewDto {

    private Long id;

    private String name;

    private String description;

    private String address1;

    private String address2;

    private UserViewDto user;

    public CompanyViewDto(final Company company) {
        this.asView(company);
    }

    private void asView(final Company company) {
        Objects.requireNonNull(company);

        this.id = company.getId();
        this.name = company.getName();
        this.description = company.getDescription();
        this.address1 = company.getAddress1();

        if (null != company.getAddress2()) {
            this.address2 = company.getAddress2();
        }

        this.user = new UserViewDto(company.getUser());
    }

}
