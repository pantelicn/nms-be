package com.opdev.post.service.noimpl;

import java.util.List;

import com.opdev.model.company.Post;

public interface PublicPostService {

    List<Post> findLatest10ByCountry(String country);

}
