package com.opdev.post.controller;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.post.dto.PostReactionDto;
import com.opdev.post.dto.PostReactionViewDto;
import com.opdev.post.service.noimpl.PostReactionService;
import com.opdev.user.UserService;

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
    private final UserService userService;

    @PutMapping
    @PreAuthorize(SpELAuthorizationExpressions.HAS_ANY_ROLE_TALENT_OR_COMPANY)
    public PostReactionViewDto addReaction(@Valid @RequestBody final PostReactionDto postReactionDto,
                                           @PathVariable final Long postId,
                                           Principal principal) {
        User found = userService.getLoggedInUser();
        if (found.getType() == UserType.TALENT) {
            postReactionService.addReactionForTalent(principal.getName(), postId, postReactionDto.getReaction());
        } else if (found.getType() == UserType.COMPANY) {
            postReactionService.addReactionForCompany(principal.getName(), postId, postReactionDto.getReaction());
        } else {
            throw new RuntimeException();
        }

        return new PostReactionViewDto(postId, postReactionDto.getReaction());
    }

    @DeleteMapping
    @PreAuthorize(SpELAuthorizationExpressions.HAS_ANY_ROLE_TALENT_OR_COMPANY)
    public void removeReaction(@Valid @RequestBody final PostReactionDto postReactionDto,
                               @PathVariable final Long postId,
                               Principal principal) {
        User found = userService.getLoggedInUser();
        if (found.getType() == UserType.TALENT) {
            postReactionService.removeReactionForTalent(principal.getName(), postId, postReactionDto.getReaction());
        } else if (found.getType() == UserType.COMPANY) {
            postReactionService.removeReactionForCompany(principal.getName(), postId, postReactionDto.getReaction());
        } else {
            throw ApiBadRequestException.message("Invalid user");
        }

    }

}
