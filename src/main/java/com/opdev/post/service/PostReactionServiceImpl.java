package com.opdev.post.service;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.post.Post;
import com.opdev.model.post.ReactionType;
import com.opdev.model.talent.Talent;
import com.opdev.post.service.noimpl.PostReactionService;
import com.opdev.post.service.noimpl.PostService;
import com.opdev.talent.TalentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    private final TalentService talentService;
    private final PostService postService;

    @Transactional
    @Override
    public void addReaction(String username, Long postId, ReactionType reaction) {
        Talent foundTalent = talentService.getByUsername(username);
        Post foundPost = postService.getById(postId);

        if (foundTalent.alreadyReacted(postId)) {
            ReactionType oldReaction = foundTalent.getReactionForPost(postId);
            foundTalent.replacePostReaction(postId, reaction);
            foundPost.removeReaction(oldReaction);
        } else {
            foundTalent.addPostReaction(postId, reaction);
        }
        foundPost.addReaction(reaction);

        talentService.save(foundTalent);
        postService.save(foundPost);
    }

    @Transactional
    @Override
    public void removeReaction(String username, Long postId, ReactionType reaction) {
        Talent foundTalent = talentService.getByUsername(username);
        Post foundPost = postService.getById(postId);

        if (!foundTalent.alreadyReacted(postId)) {
            throw new ApiBadRequestException("Invalid parameters received");
        }
        foundPost.removeReaction(reaction);
        foundTalent.removePostReaction(postId, reaction);

        talentService.save(foundTalent);
        postService.save(foundPost);
    }

}
