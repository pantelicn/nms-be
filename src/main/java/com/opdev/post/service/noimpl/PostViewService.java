package com.opdev.post.service.noimpl;

import com.opdev.model.post.Post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostViewService {

    Post getById(Long postId);

    Page<Post> findByCompanyId(Long companyId, Pageable pageable);

    Page<Post> findByCompanyIds(List<Long> companyIds, Pageable pageable);

    Page<Post> findByCountryId(Long countryId, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

}
