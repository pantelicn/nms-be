package com.opdev.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.model.post.Post;
import com.opdev.post.service.noimpl.PublicPostService;
import com.opdev.repository.PostRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicPostServiceImpl implements PublicPostService {

    private final PostRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Post> findLatest10ByCountry(@NonNull final String country) {
        return repository.findLatest10ByCountry(country);
    }
}
