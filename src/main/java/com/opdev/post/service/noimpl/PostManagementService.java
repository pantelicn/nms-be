package com.opdev.post.service.noimpl;

import com.opdev.model.post.Post;

public interface PostManagementService {

    Post add(Post post, String username);

    void delete(Long postId, Long companyId);

}
