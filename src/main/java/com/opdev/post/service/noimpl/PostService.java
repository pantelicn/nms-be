package com.opdev.post.service.noimpl;

import com.opdev.model.post.Post;

import java.util.Optional;

public interface PostService {

    Post save(final Post post);

    Post getById(final Long id);

    Optional<Post> findById(final Long id);

}
