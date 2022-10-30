package com.opdev.post.controller;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.post.Post;
import com.opdev.post.dto.PostViewDto;
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

    // TODO @nikolagudelj Find user feed

    @GetMapping
    @PreAuthorize(SpELAuthorizationExpressions.IS_AUTHENTICATED)
    public Page<PostViewDto> find(
            @RequestParam(value = "company", required = false) final Long companyId,
            @RequestParam(value = "country", required = false) final String country,
            @RequestParam(value = "city", required = false) final String city,
            @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_POSTS_PER_PAGE);
        Page<Post> foundPosts;

        if (companyId != null) {
            foundPosts = postViewService.findByCompanyId(companyId, pageable);
        } else if (city != null && country != null) {
            foundPosts = postViewService.findByLocation(country, city, pageable);
        } else throw new ApiBadRequestException("Incorrect query params");
        return foundPosts.map(PostViewDto::new);
    }

    @GetMapping("{postId}")
    public PostViewDto getById(@PathVariable @NotNull final Long postId) {
        return new PostViewDto(postViewService.getById(postId));
    }

}
