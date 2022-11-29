package com.opdev.dto;

import java.util.ArrayList;
import java.util.List;

import com.opdev.company.dto.CompanyLocationViewDto;
import com.opdev.model.company.Company;
import com.opdev.model.post.ReactionType;

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
public class CompanyViewDto {

    private Long id;

    private String name;

    private String description;

    private UserViewDto user;

    private CompanyLocationViewDto location;

    private List<Long> likedPosts = new ArrayList<>();

    public CompanyViewDto(@NonNull final Company company) {
        asView(company);
    }

    private void asView(final Company company) {
        id = company.getId();
        name = company.getName();
        description = company.getDescription();
        user = new UserViewDto(company.getUser());
        location = new CompanyLocationViewDto(company.getLocation());
        company.getPostReactions().forEach( (k,v) -> {
            if (v == ReactionType.LIKE) {
                likedPosts.add(k);
            }
        });
    }

}
