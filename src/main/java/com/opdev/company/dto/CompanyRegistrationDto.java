package com.opdev.company.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.opdev.dto.paging.RegistrationDto;
import com.opdev.model.company.Company;
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.validation.Password;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@Password
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class CompanyRegistrationDto implements RegistrationDto {

    @NotBlank
    @NonNull
    private String name;

    @NotBlank
    @NonNull
    private String description;

    @NotBlank
    @NonNull
    private String address1;

    private String address2;

    @NonNull
    @NotBlank
    @Email
    private String username;

    @NonNull
    @NotBlank
    private String password;

    @NonNull
    @NotBlank
    private String passwordConfirmed;

    @NonNull
    private CompanyLocation location;

    public Company asCompany() {

        final User user = User.builder().username(username).type(UserType.COMPANY).build();
        final Company company = Company.builder().user(user).name(name).description(description).address1(address1)
                .address2(address2).location(location).build();
        return company;
    }

}
