package com.opdev.follower;

import java.util.List;

import com.opdev.model.user.Follower;

public interface FollowerService {

    Follower followCompany(String username, Long companyId);

    List<Follower> findByFollower(String username);

    void unfollowCompany(String username, Long companyId);

}
