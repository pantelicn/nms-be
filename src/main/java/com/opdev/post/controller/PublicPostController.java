package com.opdev.post.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.model.post.Post;
import com.opdev.post.dto.PostViewDto;
import com.opdev.post.service.noimpl.PublicPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/public/posts")
@RequiredArgsConstructor
public class PublicPostController {

    private final PublicPostService service;

    @GetMapping
    public List<PostViewDto> findLatest10ByCountry(@RequestParam String country) {
        List<Post> found = service.findLatest10ByCountry(country);
        return found.stream().map(PostViewDto::new).collect(Collectors.toList());
    }

}
