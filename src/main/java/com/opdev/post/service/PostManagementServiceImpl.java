package com.opdev.post.service;

import com.opdev.model.post.Post;
import com.opdev.post.service.noimpl.PostManagementService;
import com.opdev.repository.PostRepository;
import com.opdev.subscription.usage.ProductUsageService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PostManagementServiceImpl implements PostManagementService {

    private final PostRepository repository;
    private final ProductUsageService productUsageService;

    @Override
    @Transactional
    public Post add(final Post post, final String username) {
        productUsageService.decreaseNumberOfRemainingPosts(username);
        return repository.save(post);
    }

    @Override
    @Transactional
    public void delete(final Long postId, final Long companyId) {
        repository.deleteByIdAndCompanyId(postId, companyId);
    }

}
