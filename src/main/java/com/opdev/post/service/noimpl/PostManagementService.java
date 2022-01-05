package com.opdev.post.service.noimpl;

import com.opdev.model.company.Post;

public interface PostManagementService {

    Post add(Post post);

    void delete(Long postId, Long companyId);

}
