package com.opdev.post.service.noimpl;

import com.opdev.model.company.Post;

import java.util.List;

public interface PostViewService {

    Post getById(Long postId);

    List<Post> findByCompanyId(Long companyId);

    List<Post> findByCompanyIds(List<Long> companyIds);

    List<Post> findByLocation(String country, String city);

    void like(Long postId, String username);

    void unlike(Long postId, String username);

}
