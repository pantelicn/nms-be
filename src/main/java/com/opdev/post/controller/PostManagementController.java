package com.opdev.post.controller;

import com.opdev.company.service.CompanyService;
import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.model.company.Company;
import com.opdev.model.company.Post;
import com.opdev.post.service.noimpl.PostManagementService;
import com.opdev.post.dto.PostAddDto;
import com.opdev.post.dto.PostViewDto;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@RestController
@RequestMapping("v1/companies/{username}/posts")
public class PostManagementController {

    private final PostManagementService postManagementService;
    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize(SpELAuthorizationExpressions.asMatchingCompanyOrAdmin)
    @ResponseStatus(HttpStatus.CREATED)
    public PostViewDto add(@RequestBody @Valid final PostAddDto dto, @PathVariable final String username) {
        final Company company = companyService.getByUsername(username);

        Post postToBeCreated = dto.asPostFromCompany(company);
        postToBeCreated.setCreatedBy(company.getUser());

        final Post createdPost = postManagementService.add(postToBeCreated);
        return new PostViewDto(createdPost);
    }

    @DeleteMapping("{postId}")
    @PreAuthorize(SpELAuthorizationExpressions.asMatchingCompanyOrAdmin)
    public void deleteById(@PathVariable @NotNull final Long postId, @PathVariable final String username) {
        final Long companyId = companyService.getByUsername(username).getId();
        postManagementService.delete(postId, companyId);
    }

}
