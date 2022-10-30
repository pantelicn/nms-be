package com.opdev.post.controller;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.post.dto.PostReactionDto;
import com.opdev.post.dto.PostReactionViewDto;
import com.opdev.post.service.noimpl.PostReactionService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("v1/posts/{postId}/reactions")
@AllArgsConstructor
public class PostReactionController {

    private final PostReactionService postReactionService;

    @PutMapping
    @PreAuthorize(SpELAuthorizationExpressions.IS_TALENT)
    public PostReactionViewDto addReaction(@Valid @RequestBody final PostReactionDto postReactionDto,
                                           @PathVariable final Long postId,
                                           Principal principal) {
        postReactionService.addReaction(principal.getName(), postId, postReactionDto.getReaction());
        return new PostReactionViewDto(postId, postReactionDto.getReaction());
    }

    @DeleteMapping
    @PreAuthorize(SpELAuthorizationExpressions.IS_TALENT)
    public void removeReaction(@Valid @RequestBody final PostReactionDto postReactionDto,
                               @PathVariable final Long postId,
                               Principal principal) {
        postReactionService.removeReaction(principal.getName(), postId, postReactionDto.getReaction());
    }

}
