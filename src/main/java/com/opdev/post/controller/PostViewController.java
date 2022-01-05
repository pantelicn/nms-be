package com.opdev.post.controller;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.model.company.Post;
import com.opdev.post.dto.PostViewDto;
import com.opdev.post.service.noimpl.PostViewService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/posts")
@AllArgsConstructor
public class PostViewController {

    private final PostViewService postViewService;

    // TODO @nikolagudelj Find user feed

    @GetMapping
    @PreAuthorize(SpELAuthorizationExpressions.isAuthenticated)
    public List<PostViewDto> find(
            @RequestParam("company") final Long companyId,
            @RequestParam("country") final String country,
            @RequestParam("city") final String city
    ) {
        // Searches by company and location are disjunctive
        List<Post> foundPosts = companyId == null ?
                postViewService.findByLocation(country, city)
                : postViewService.findByCompanyId(companyId);
        return foundPosts
                .stream()
                .map(PostViewDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("{postId}")
    public PostViewDto getById(@PathVariable @NotNull final Long postId) {
        return new PostViewDto(postViewService.getById(postId));
    }

    @PutMapping("{postId}/user/{username}/like")
    @PreAuthorize(SpELAuthorizationExpressions.asMatchingTalentOrCompany)
    public void like(@PathVariable @NotNull final Long postId, @PathVariable @NotNull final String username) {
        postViewService.like(postId, username);
    }

    @PutMapping("{postId}/user/{username}/unlike")
    @PreAuthorize(SpELAuthorizationExpressions.asMatchingTalentOrCompany)
    public void unlike(@PathVariable @NotNull final Long postId, @PathVariable @NotNull final String username) {
        postViewService.unlike(postId, username);
    }

}
