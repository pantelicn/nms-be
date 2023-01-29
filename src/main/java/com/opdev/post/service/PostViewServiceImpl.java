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

    @Override
    @Transactional(readOnly = true)
    public Page<Post> findByCompanyIds(final List<Long> companyIds, Pageable pageable) {
        return repository.findByCompanyIds(companyIds, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> findByCountryId(Long countryId, Pageable pageable) {
        return repository.findByCountryId(countryId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

}
