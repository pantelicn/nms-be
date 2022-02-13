package com.opdev.post.dto;

import com.opdev.model.post.ReactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostReactionViewDto {

    private Long postId;

    private ReactionType reaction;

}
