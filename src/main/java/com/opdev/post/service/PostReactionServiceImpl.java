package com.opdev.post.service;

import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.company.Company;
import com.opdev.model.post.Post;
import com.opdev.model.post.ReactionType;
import com.opdev.model.subscription.ProductUsage;
import com.opdev.model.talent.Talent;
import com.opdev.post.service.noimpl.PostReactionService;
import com.opdev.post.service.noimpl.PostService;
import com.opdev.subscription.usage.ProductUsageService;
import com.opdev.talent.TalentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    private final TalentService talentService;
    private final PostService postService;
    private final CompanyService companyService;
    private final ProductUsageService productUsageService;

    @Transactional
    @Override
    public void addReactionForTalent(String username, Long postId, ReactionType reaction) {
        Talent foundTalent = talentService.getByUsername(username);
        Post foundPost = postService.getById(postId);

        if (!foundTalent.alreadyReacted(postId, reaction)) {
            foundTalent.addPostReaction(postId, reaction);
            foundPost.addReaction(reaction);
            if (reaction == ReactionType.AWARD) {
                checkAndGiveAward(foundPost);
            }
            talentService.save(foundTalent);
            postService.save(foundPost);
        }
    }

    @Transactional
    @Override
    public void removeReactionForTalent(String username, Long postId, ReactionType reaction) {
        Talent foundTalent = talentService.getByUsername(username);
        Post foundPost = postService.getById(postId);

        if (foundTalent.alreadyReacted(postId, reaction)) {
            foundPost.removeReaction(reaction);
            foundTalent.removePostReaction(postId, reaction);

            talentService.save(foundTalent);
            postService.save(foundPost);
        }
    }

    @Override
    @Transactional
    public void addReactionForCompany(final String username, final Long postId, final ReactionType reaction) {
        Company foundCompany = companyService.getByUsername(username);
        Post foundPost = postService.getById(postId);

        if (!foundCompany.alreadyReacted(postId, reaction)) {
            foundCompany.addPostReaction(postId, reaction);
            foundPost.addReaction(reaction);
            if (reaction == ReactionType.AWARD) {
                checkAndGiveAward(foundPost);
            }
            companyService.save(foundCompany);
            postService.save(foundPost);
        }
    }

    @Override
    @Transactional
    public void removeReactionForCompany(final String username, final Long postId, final ReactionType reaction) {
        Company foundCompany = companyService.getByUsername(username);
        Post foundPost = postService.getById(postId);

        if (foundCompany.alreadyReacted(postId, reaction)) {
            foundPost.removeReaction(reaction);
            foundCompany.removePostReaction(postId, reaction);

            companyService.save(foundCompany);
            postService.save(foundPost);
        }
    }

    private void checkAndGiveAward(Post post) {
        if (post.getAwards() >= 1 && !post.isAwardEarned()) {
            post.setAwardEarned(true);
            ProductUsage postProductUsage = post.getCompany().getProductUsages().stream().filter(productUsage -> productUsage.getProduct().getName().equals("Post")).findFirst().orElseThrow(() -> new RuntimeException("Product does not exists!"));
            postProductUsage.increaseRemaining();
            productUsageService.save(postProductUsage);
        }
    }

}
