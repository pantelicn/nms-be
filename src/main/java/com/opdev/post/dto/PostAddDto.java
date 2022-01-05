package com.opdev.post.dto;

import com.opdev.model.company.Company;
import com.opdev.model.company.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostAddDto {

    @NotNull
    private String description;
    // TODO @nikolagudelj We may want to check for our custom image hosting domain
    @Pattern(regexp = "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$")
    private String url;

    public Post asPostFromCompany(Company company) {
        return Post.builder()
                .company(company)
                .description(description)
                .url(url)
                .build();
    }
}
