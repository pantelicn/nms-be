package com.opdev.post.service;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.post.Post;
import com.opdev.post.service.noimpl.PostViewService;
import com.opdev.repository.PostRepository;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PostViewServiceImpl implements PostViewService {

    private final PostRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Post getById(final Long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> ApiEntityNotFoundException.builder().entity("post").build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> findByCompanyId(final Long companyId, final Pageable pageable) {
        return repository.findByCompanyId(companyId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Post> findUserFeedPosts(final Long userId) {
        // TODO @nikolagudelj Fetch companies which user follows from the db
        return findByCompanyIds(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findByCompanyIds(final List<Long> companyIds) {
        return repository.findByCompanyIds(companyIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> findByLocation(String country, String city, Pageable pageable) {
        return repository.findByCountryAndCity(country, city, pageable);
    }

}
