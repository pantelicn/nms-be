package com.opdev.post.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.follower.FollowerService;
import com.opdev.model.post.Post;
import com.opdev.model.user.Follower;
import com.opdev.post.dto.PostViewDto;
import com.opdev.post.dto.PostsType;
import com.opdev.post.service.noimpl.PostReactionService;
import com.opdev.post.service.noimpl.PostViewService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("v1/posts")
@AllArgsConstructor
public class PostViewController {

    private static final int MAX_POSTS_PER_PAGE = 30;

    private final PostViewService postViewService;
    private final PostReactionService postReactionService;
    private final FollowerService followerService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<PostViewDto> find(
            @RequestParam PostsType postsType,
            @RequestParam(required = false) final Long companyId,
            @RequestParam(required = false) final Long countryId,
            @RequestParam(defaultValue = "0") Integer page, Principal user) {
        Pageable pageable = PageRequest.of(page, MAX_POSTS_PER_PAGE);
        Page<Post> foundPosts;

        if (postsType == PostsType.GLOBAL) {
            foundPosts = postViewService.findAll(pageable);
        } else if (postsType == PostsType.FOLLOWING) {
            List<Follower> following =  followerService.findByFollower(user.getName());
            foundPosts = postViewService.findByCompanyIds(following.stream().map(follower -> follower.getCompany().getId()).collect(Collectors.toList()), pageable);
        } else if (postsType == PostsType.COUNTRY && countryId != null) {
            foundPosts = postViewService.findByCountryId(countryId, pageable);
        } else if (postsType == PostsType.COMPANY && companyId != null) {
            foundPosts = postViewService.findByCompanyId(companyId, pageable);
        } else {
            throw ApiBadRequestException.message("Not able to pull posts for provided params");
        }

        return foundPosts.map(PostViewDto::new);
    }

    @GetMapping("{postId}")
    public PostViewDto getById(@PathVariable @NotNull final Long postId) {
        return new PostViewDto(postViewService.getById(postId));
    }

}
