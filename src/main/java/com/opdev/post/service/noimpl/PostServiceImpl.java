package com.opdev.post.service.noimpl;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.post.Post;
import com.opdev.model.talent.Talent;
import com.opdev.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public Post getById(Long id) {
        return findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity(Talent.class.getSimpleName()).id(id.toString()).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    @Transactional
    public Post save(Post post) {
        Objects.requireNonNull(post);
        return postRepository.save(post);
    }
}
