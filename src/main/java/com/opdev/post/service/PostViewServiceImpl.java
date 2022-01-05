package com.opdev.post.service;

import com.opdev.authentication.UserService;
import com.opdev.model.company.Post;
import com.opdev.model.user.User;
import com.opdev.post.service.noimpl.PostViewService;
import com.opdev.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PostViewServiceImpl implements PostViewService {

    private final PostRepository repository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Post getById(final Long postId) {
        return repository.getById(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findByCompanyId(final Long companyId) {
        return repository.findByCompanyId(companyId);
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
    public List<Post> findByLocation(String country, String city) {
        return repository.findByLocation(country, city);
    }

    @Override
    @Transactional
    public void like(Long postId, String username) {
        User user = userService.findByUsername(username).orElseThrow();
        if (user.getLikedPosts().contains(postId)) {
            return;
        }

        Post foundPost = repository.getById(postId);

        foundPost.addLike();
        user.getLikedPosts().add(postId);

        repository.save(foundPost);
        userService.save(user);
    }

    @Override
    @Transactional
    public void unlike(Long postId, String username) {
        User user = userService.findByUsername(username).orElseThrow();
        if (!user.getLikedPosts().contains(postId)) {
            return;
        }

        Post foundPost = repository.getById(postId);

        foundPost.removeLike();
        user.getLikedPosts().remove(postId);

        repository.save(foundPost);
        userService.save(user);
    }

}
