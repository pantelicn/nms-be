package com.opdev.post.dto;

import com.opdev.model.company.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostViewDto {
    private Long id;
    private String description;
    private String url;
    private Integer likes;

    public PostViewDto(final Post post) {
        this.id = post.getId();
        this.description = post.getDescription();
        this.url = post.getUrl();
        this.likes = post.getLikes();
    }

}
