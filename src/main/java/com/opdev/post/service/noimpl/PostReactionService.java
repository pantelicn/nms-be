package com.opdev.post.service.noimpl;

import com.opdev.model.post.ReactionType;

public interface PostReactionService {

    void addReactionForTalent(String username, Long postId, ReactionType reaction);

    void removeReactionForTalent(String username, Long postId, ReactionType reaction);

    void addReactionForCompany(String username, Long postId, ReactionType reaction);

    void removeReactionForCompany(String username, Long postId, ReactionType reaction);

}
