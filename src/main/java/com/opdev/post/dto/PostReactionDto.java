package com.opdev.post.dto;

import com.opdev.model.post.ReactionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostReactionDto {

    @NonNull
    @NotNull
    private ReactionType reaction;

}
