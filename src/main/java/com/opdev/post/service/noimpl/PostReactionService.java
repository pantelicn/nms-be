package com.opdev.post.service.noimpl;

import com.opdev.model.post.ReactionType;

public interface PostReactionService {

    void addReaction(String username, Long postId, ReactionType reaction);

    void removeReaction(String username, Long postId, ReactionType reaction);

}
