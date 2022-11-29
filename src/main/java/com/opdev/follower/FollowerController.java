package com.opdev.follower;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.follower.dto.CreateFollowerDto;
import com.opdev.follower.dto.UnfollowRequestDto;
import com.opdev.model.user.Follower;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/followers")
@RequiredArgsConstructor
public class FollowerController {

    private final FollowerService service;

    @PostMapping
    @PreAuthorize(SpELAuthorizationExpressions.HAS_ANY_ROLE_TALENT_OR_COMPANY)
    @ResponseStatus(HttpStatus.CREATED)
    public void follow(@RequestBody CreateFollowerDto request, Principal user) {
        service.followCompany(user.getName(), request.getCompanyId());
    }

    @GetMapping("following")
    @PreAuthorize(SpELAuthorizationExpressions.HAS_ANY_ROLE_TALENT_OR_COMPANY)
    public List<Long> following(Principal user) {
        List<Follower> found = service.findByFollower(user.getName());
        return found.stream().map(follower -> follower.getCompany().getId()).collect(Collectors.toList());
    }

    @DeleteMapping
    @PreAuthorize(SpELAuthorizationExpressions.HAS_ANY_ROLE_TALENT_OR_COMPANY)
    public void unfollow(@RequestBody UnfollowRequestDto request, Principal user) {
        service.unfollowCompany(user.getName(), request.getCompanyId());
    }

}
