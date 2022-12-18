package com.opdev.dto;

import java.util.ArrayList;
import java.util.List;

import com.opdev.company.dto.CompanyLocationViewDto;
import com.opdev.contact.dto.ContactViewDto;
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

    private CompanyLocationViewDto location;

    private String profileImage;

    private List<ContactViewDto> contacts = new ArrayList<>();

    public CompanyPublicViewDto(final Company company) {
        this.asView(company);
    }

    private void asView(@NonNull final Company company) {
        id = company.getId();
        name = company.getName();
        description = company.getDescription();
        location = new CompanyLocationViewDto(company.getLocation());
        profileImage = company.getProfileImage();
        company.getContacts().forEach(contact -> contacts.add(new ContactViewDto(contact)));
    }

}
