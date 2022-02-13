package com.opdev.post.dto;

import com.opdev.model.company.Company;
import com.opdev.model.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class PostAddDto {

    @NotNull
    private String description;
    // TODO @nikolagudelj We may want to check for our custom image hosting domain
    private String url;

    public Post asPostFromCompany(Company company) {
        return Post.builder()
                .company(company)
                .description(description)
                .country(company.getLocation().getCountry())
                .url(url)
                .build();
    }
}
