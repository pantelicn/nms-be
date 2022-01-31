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
import lombok.ToString;

@Getter
@Builder
@Password
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class CompanyRegistrationDto implements RegistrationDto {

    @NotBlank
    @NonNull
    private String name;

    @NotBlank
    @NonNull
    private String description;

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
    private CompanyLocationDto location;

    public Company asCompany() {

        final User user = User.builder()
                .username(username)
                .password(password)
                .type(UserType.COMPANY)
                .build();
        final Company company = Company.builder()
                .user(user)
                .name(name)
                .description(description)
                .location(location.asCompanyLocation())
                .build();
        return company;
    }

}
