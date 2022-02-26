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
    private String content;
    @NotNull
    private String title;
    private String url;

    public Post asPostFromCompany(Company company) {
        return Post.builder()
                .company(company)
                .content(content)
                .title(title)
                .country(company.getLocation().getCountry())
                .url(url)
                .build();
    }
}
