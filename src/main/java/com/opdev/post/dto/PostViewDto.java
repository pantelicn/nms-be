package com.opdev.post.dto;

import com.opdev.dto.CompanyPublicViewDto;
import com.opdev.model.post.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostViewDto {
    private Long id;
    private String content;
    private String title;
    private String url;
    private Integer likes;
    private CompanyPublicViewDto company;
    private Instant createdOn;

    public PostViewDto(final Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.title = post.getTitle();
        this.url = post.getUrl();
        this.likes = post.getLikes();
        this.company = new CompanyPublicViewDto(post.getCompany());
        this.createdOn = post.getCreatedOn();
    }

}
